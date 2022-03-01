package dev.cuican.staypro.module.modules.movement;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.PacketEvents;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "BoatFly", category = Category.MOVEMENT, description = "Weeeeeeee")
public class BoatFly extends Module {
    public static BoatFly INSTANCE;
    public Setting<Double> speed = setting("Speed",5,0.1,100);
    public Setting<Double> VSpeed = setting("VerticalSpeed",5,0.1,100);
    public Setting<Boolean> noKick = setting("NoKick",false);
    public Setting<Boolean> packet = setting("Packet",false);
    public Setting<Integer> packets = setting("Packets",3,1,5);
    public Setting<Double> interact = setting("Delay",3d,1,10);
    private int teleportID;

    public BoatFly() {
        INSTANCE = this;
    }

    @Override
    public void onTick() {
        if (fullNullCheck() || mc.player.getRidingEntity() == null) {
            return;
        }
        //mc.player.getRidingEntity();
        mc.player.getRidingEntity().setNoGravity(true);
        mc.player.getRidingEntity().motionY = 0.0;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.getRidingEntity().onGround = false;
            mc.player.getRidingEntity().motionY = (this.VSpeed.getValue() / 10.0);
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.getRidingEntity().onGround = false;
            mc.player.getRidingEntity().motionY = (this.VSpeed.getValue() / -10.0);

        }
        final double[] normalDir = this.directionSpeed(this.speed.getValue() / 2.0);
        if (mc.player.movementInput.moveStrafe != 0.0f || mc.player.movementInput.moveForward != 0.0f) {
            mc.player.getRidingEntity().motionX = normalDir[0];
            mc.player.getRidingEntity().motionZ = normalDir[1];
        } else {
            mc.player.getRidingEntity().motionX = 0.0;
            mc.player.getRidingEntity().motionZ = 0.0;
        }
        if (this.noKick.getValue()) {
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                if (mc.player.ticksExisted % 8 < 2) {
                    mc.player.getRidingEntity().motionY = -0.03999999910593033;
                }
            } else if (mc.player.ticksExisted % 8 < 4) {
                mc.player.getRidingEntity().motionY = -0.07999999821186066;
            }
        }
        this.handlePackets(mc.player.getRidingEntity().motionX, mc.player.getRidingEntity().motionY, mc.player.getRidingEntity().motionZ);
    }

    public void handlePackets(final double x, final double y, final double z) {
        if (this.packet.getValue()) {
            final Vec3d vec = new Vec3d(x, y, z);
            if (mc.player.getRidingEntity() == null) {
                return;
            }
            final Vec3d position = mc.player.getRidingEntity().getPositionVector().add(vec);
            mc.player.getRidingEntity().setPosition(position.x, position.y, position.z);
            mc.player.connection.sendPacket(new CPacketVehicleMove(mc.player.getRidingEntity()));
            for (int i = 0; i < this.packets.getValue(); ++i) {
                mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportID++));
            }
        }
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketVehicleMove && mc.player.isRiding() && BoatFly.mc.player.ticksExisted / this.interact.getValue() == 0) {
            mc.playerController.interactWithEntity(mc.player, mc.player.ridingEntity, EnumHand.OFF_HAND);
        }
        if ((event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketInput) && mc.player.isRiding()) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onReceivePacket(final PacketEvents.Receive event) {
        if (event.getPacket() instanceof SPacketMoveVehicle && mc.player.isRiding()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.teleportID = ((SPacketPlayerPosLook) event.getPacket()).teleportId;
        }
    }

    private double[] directionSpeed(final double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }
}
