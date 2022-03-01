package dev.cuican.staypro.module.modules.movement;


import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.PlayerInteractEvent;
import dev.cuican.staypro.event.events.network.EntityJoinWorldEvent;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.mixin.network.MixinBlockSoulSand;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;




/**
 * Created by 086 on 15/12/2017.
 * Updated by S-B99 on 21/03/20
 * @see MixinBlockSoulSand
 * @see net.minecraft.client.entity.EntityPlayerSP#onLivingUpdate()
 */
@ModuleInfo(name = "NoSlowDown", category = Category.MOVEMENT, description = "Prevents being slowed down when using an item or going through cobwebs")
public class NoSlowDown extends Module {
    public Setting<Boolean> soulSand = setting("SoulSand",true);
    public Setting<Boolean> strict = setting("Strict",true);
    public Setting<Boolean> sneakPacket = setting("SneakPacket",true);
    public Setting<Boolean> webs = setting("Webs",true);
    public final Setting<Integer> webHorizontalFactor = setting("WebHSpeed",2,0,100);
    public final Setting<Integer> webVerticalFactor = setting("WebVSpeed",2,0,100);
    private static NoSlowDown INSTANCE = new NoSlowDown();
    private boolean sneaking = false;
    private static final KeyBinding[] keys = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint};

    public NoSlowDown() {
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static NoSlowDown getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoSlowDown();
        }
        return INSTANCE;
    }

    @Override
    public void onTick() {
    if (this.webs.getValue() && ModuleManager.getModuleByName("Flight").isDisabled() && mc.player.isInWeb) {
            mc.player.motionX *= this.webHorizontalFactor.getValue();
            mc.player.motionZ *= this.webHorizontalFactor.getValue();
            mc.player.motionY *= this.webVerticalFactor.getValue();
        }
        if (this.sneaking && !mc.player.isHandActive() && this.sneakPacket.getValue()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.sneaking = false;
        }
    }


    @Listener
    public void onWorldEvent(EntityJoinWorldEvent event) {
        if (this.sneakPacket.getValue() && this.sneaking && !mc.player.isHandActive()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.sneaking = false;
        }
    }



    @Listener
    public void onInput(PlayerInteractEvent event) {
        if (mc.player.isHandActive() && !mc.player.isRiding()) {
            event.getMovementInput().moveStrafe *= 5.0f;
            event.getMovementInput().moveForward *= 5.0f;
        }
    }


    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && this.strict.getValue() && mc.player.isHandActive() && !mc.player.isRiding()) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)), EnumFacing.DOWN));
        }

    }
}
