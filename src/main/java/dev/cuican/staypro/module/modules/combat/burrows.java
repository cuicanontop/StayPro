package dev.cuican.staypro.module.modules.combat;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.BurrowUtil;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.Timer;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import dev.cuican.staypro.utils.position.PositionUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "StayProBurrowMax+", category = Category.COMBAT, description = "Stable 3 grid burrow")
public class burrows extends Module {
    private final Setting<Boolean> breakCrystal = setting("BreakCrystal", true);
    private final Setting<Boolean> rotate = setting("Rotate", true);
    public Setting<Boolean> center = setting("TPCenter", false);
    public int teleportID;
    @Override
    public void onEnable() {
        if (breakCrystal.getValue())BurrowUtil.back();
        if(BurrowUtil.isInsideBlock())toggle();
    }
    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            teleportID = packet.getTeleportId();
        }
    }

    @Override
    public void onTick() {
        if (mc.player != null && mc.world != null) {
            if(BurrowUtil.isInsideBlock())toggle();
            if(!mc.player.onGround) return;
            mc.player.connection.sendPacket(new CPacketConfirmTeleport(teleportID));
            if( BurrowUtil.burrow(rotate.getValue(),center.getValue())) toggle();
        }
    }
    @Override
    public void onRenderTick() {
        if (breakCrystal.getValue())BurrowUtil.back();
    }



}

