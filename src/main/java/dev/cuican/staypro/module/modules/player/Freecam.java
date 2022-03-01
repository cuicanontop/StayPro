package dev.cuican.staypro.module.modules.player;


import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.MoveEvent;
import dev.cuican.staypro.event.events.client.PacketEvents;
import dev.cuican.staypro.event.events.client.PushEvent;
import dev.cuican.staypro.event.events.network.EntityJoinWorldEvent;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.MathUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by 086 on 22/12/2017.
 */
@ModuleInfo(name = "Freecam", category = Category.PLAYER, description = "Leave your body and trascend into the realm of the gods")
public class Freecam extends Module {

    public static Freecam INSTANCE;

    static {
        INSTANCE = new Freecam();
    }

    public Setting<Boolean> CancelPackes = setting("CancelPackets", true);
    public Setting<Boolean> toggleRStep = setting("ToggleRStep", true);
    public Setting<Double> speed = setting("Speed", 1, 0.1, 10);
    public boolean firstStart = false;
    public double posX, posY, posZ;
    public float pitch, yaw;
    public EntityOtherPlayerMP clonedPlayer;
    public boolean isRidingEntity;
    public Entity ridingEntity;

    public Freecam() {
        this.setInstance();
    }

    public static Freecam getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Freecam();
        }
        return INSTANCE;
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (!CancelPackes.getValue())
            return;
        if (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketInput) {
            event.isCancelled();
        }
    }

    public void setInstance() {
        INSTANCE = this;
    }

    @Listener
    public void onPush(PushEvent event) {
        event.setCanceled(true);
    }

    @Override
    public void onEnable() {
        firstStart = true;
        if (this.toggleRStep.getValue()) {
            ModuleManager.getModuleByName("ReverseStep").disable();
        }
        if (mc.player != null) {
            new Thread(() -> {
                isRidingEntity = mc.player.getRidingEntity() != null;

                if (mc.player.getRidingEntity() == null) {
                    posX = mc.player.posX;
                    posY = mc.player.posY;
                    posZ = mc.player.posZ;
                } else {
                    ridingEntity = mc.player.getRidingEntity();
                    mc.player.dismountRidingEntity();
                }

                pitch = mc.player.rotationPitch;
                yaw = mc.player.rotationYaw;

                clonedPlayer = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
                clonedPlayer.copyLocationAndAnglesFrom(mc.player);
                clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
                mc.world.addEntityToWorld(-101, clonedPlayer);
                mc.player.capabilities.isFlying = true;
                mc.player.capabilities.setFlySpeed((float) (speed.getValue() / 100f));
                mc.player.noClip = true;
            }).start();
        }
    }

    @Override
    public void onDisable() {
        if (this.toggleRStep.getValue()) {
            ModuleManager.getModuleByName("ReverseStep").enable();
        }
        EntityPlayer localPlayer = mc.player;
        if (localPlayer != null) {
            mc.player.setPositionAndRotation(posX, posY, posZ, yaw, pitch);
            mc.world.removeEntityFromWorld(-101);
            clonedPlayer = null;
            posX = posY = posZ = 0.D;
            pitch = yaw = 0.f;
            mc.player.capabilities.isFlying = false; //getModManager().getMod("ElytraFlight").isEnabled();
            mc.player.capabilities.setFlySpeed(0.05f);
            mc.player.noClip = false;
            mc.player.motionX = mc.player.motionY = mc.player.motionZ = 0.f;

            if (isRidingEntity) {
                mc.player.startRiding(ridingEntity, true);
            }
        }
    }

    @Override
    public void onTick() {
        if (toggleRStep.getValue()) {
            if (ModuleManager.getModuleByName("ReverseStep").isEnabled()) {
                return;
            }
        }
        new Thread(() -> {
            mc.player.noClip = true;
            mc.player.setVelocity(0, 0, 0);
            final double[] dir = MathUtil.directionSpeed(this.speed.getValue());
            if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                mc.player.motionX = dir[0];
                mc.player.motionZ = dir[1];
            } else {
                mc.player.motionX = 0;
                mc.player.motionZ = 0;
            }
            mc.player.setSprinting(false);
            clonedPlayer.prevRotationPitch = mc.player.prevRotationPitch;
            clonedPlayer.rotationPitch = mc.player.rotationPitch;
            clonedPlayer.rotationYaw = mc.player.rotationYaw;
            clonedPlayer.renderYawOffset = mc.player.renderYawOffset;
            clonedPlayer.rotationYawHead = mc.player.rotationYawHead;
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.motionY += this.speed.getValue();
            }
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.motionY -= this.speed.getValue();
            }
        }).start();
    }

    @Listener
    public void move(MoveEvent event) {
        if (firstStart) {
            event.setX(0);
            event.setY(0);
            event.setZ(0);
            firstStart = false;
        }
        mc.player.noClip = true;
    }

    @Listener
    public void onWorldEvent(EntityJoinWorldEvent event) {
        if (event.getEntity() == mc.player) {
            toggle();
        }
    }

}
