package dev.cuican.staypro.module.modules.movement;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.MoveEvent;
import dev.cuican.staypro.event.events.client.PushEvent;
import dev.cuican.staypro.event.events.client.UpdateWalkingPlayerEvent;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import io.netty.util.internal.ConcurrentSet;

import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;

import java.util.Set;
@ModuleInfo(name = "Phase", category = Category.MOVEMENT,description = "Makes you able to phase through blocks.")
public class Phase
        extends Module {
    private static Phase INSTANCE = new Phase();
    public Setting<String> mode = setting("Mode", "PACKETFLY",listOf("PACKETFLY"));
    public Setting<String> type = setting("Type", "SETBACK",listOf( "NONE", "SETBACK")).whenAtMode( mode, "PACKETFLY");
    public Setting<Integer> yMove = setting("YMove", 625, 1, 1000);
    public Setting<Boolean> extra = setting("ExtraPacket", true);
    public Setting<Integer> offset = setting("Offset", 1337, -1337, 1337);
    public Setting<Boolean> fallPacket = setting("FallPacket", true);
    public Setting<Boolean> teleporter = setting("Teleport", true);
    public Setting<Boolean> boundingBox = setting("BoundingBox",true);
    public Setting<Integer> teleportConfirm = setting("Confirm", 2, 0, 4);
    public Setting<Boolean> ultraPacket = setting("DoublePacket", false);
    public Setting<Boolean> updates = setting("Update", false);
    public Setting<Boolean> setOnMove = setting("SetMove", false);
    public Setting<Boolean> cliperino = setting("NoClip", false);
    public Setting<Boolean> scanPackets = setting("ScanPackets", false);
    public Setting<Boolean> resetConfirm = setting("Reset", false);
    public Setting<Boolean> posLook = setting("PosLook", false);
    public Setting<Boolean> cancel = setting("Cancel", false);
    public Setting<Boolean> cancelType = setting("SetYaw", false);
    public Setting<Boolean> onlyY = setting("OnlyY", Boolean.FALSE);
    public Setting<Integer> cancelPacket = setting("Packets", 20, 0, 20);
    private final Set<CPacketPlayer> packets = new ConcurrentSet();
    private boolean teleport = true;
    private int teleportIds = 0;
    private int posLookPackets;

    public Phase() {
        this.setInstance();
    }
    @Override
    public void onTick() {
        if (fullNullCheck()) {
disable();
        }}
    public static Phase getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Phase();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        this.packets.clear();
        this.posLookPackets = 0;
        if (Phase.mc.player != null) {
            if (this.resetConfirm.getValue()) {
                this.teleportIds = 0;
            }
            Phase.mc.player.noClip = false;
        }
    }

    @Override
    public String getModuleInfo() {
        return name;
    }

    @Listener
    public void onMove(MoveEvent event) {
        if (this.setOnMove.getValue() && this.type.getValue() .equals("SETBACK") && event.getStage() == 0 && !mc.isSingleplayer() && this.mode.getValue().equals("PACKETFLY")) {
            event.setX(Phase.mc.player.motionX);
            event.setY(Phase.mc.player.motionY);
            event.setZ(Phase.mc.player.motionZ);
            if (this.cliperino.getValue()) {
                Phase.mc.player.noClip = true;
            }
        }
        if (this.type.getValue().equals("NONE") || event.getStage() != 0 || mc.isSingleplayer() || !this.mode.getValue().equals("PACKETFLY")) {
            return;
        }
        if (!this.boundingBox.getValue() && !this.updates.getValue()) {
            this.doPhase(event);
        }
    }

    @Listener
    public void onPush(PushEvent event) {
        if (event.getStage() == 1 && !this.type.getValue().equals("NONE")) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onMove(UpdateWalkingPlayerEvent event) {
        if (Phase.fullNullCheck() || event.getStage() != 0 || !this.type.getValue().equals("SETBACK") || !this.mode.getValue().equals("PACKETFLY")) {
            return;
        }
        if (this.boundingBox.getValue()) {
            this.doBoundingBox();
        } else if (this.updates.getValue()) {
            this.doPhase(null);
        }
    }

    private void doPhase(MoveEvent event) {
        if (this.type.getValue().equals("SETBACK") && !this.boundingBox.getValue()) {
            double[] dirSpeed = this.getMotion(this.teleport ? (double) this.yMove.getValue() / 10000.0 : (double) (this.yMove.getValue() - 1) / 10000.0);
            double posX = Phase.mc.player.posX + dirSpeed[0];
            double posY = Phase.mc.player.posY + (Phase.mc.gameSettings.keyBindJump.isKeyDown() ? (this.teleport ? (double) this.yMove.getValue() / 10000.0 : (double) (this.yMove.getValue() - 1) / 10000.0) : 1.0E-8) - (Phase.mc.gameSettings.keyBindSneak.isKeyDown() ? (this.teleport ? (double) this.yMove.getValue() / 10000.0 : (double) (this.yMove.getValue() - 1) / 10000.0) : 2.0E-8);
            double posZ = Phase.mc.player.posZ + dirSpeed[1];
            CPacketPlayer.PositionRotation packetPlayer = new CPacketPlayer.PositionRotation(posX, posY, posZ, Phase.mc.player.rotationYaw, Phase.mc.player.rotationPitch, false);
            this.packets.add(packetPlayer);
            Phase.mc.player.connection.sendPacket(packetPlayer);
            if (this.teleportConfirm.getValue() != 3) {
                Phase.mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportIds - 1));
                ++this.teleportIds;
            }
            if (this.extra.getValue()) {
                CPacketPlayer.PositionRotation packet = new CPacketPlayer.PositionRotation(Phase.mc.player.posX, (double) this.offset.getValue() + Phase.mc.player.posY, Phase.mc.player.posZ, Phase.mc.player.rotationYaw, Phase.mc.player.rotationPitch, true);
                this.packets.add(packet);
                Phase.mc.player.connection.sendPacket(packet);
            }
            if (this.teleportConfirm.getValue() != 1) {
                Phase.mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportIds + 1));
                ++this.teleportIds;
            }
            if (this.ultraPacket.getValue()) {
                CPacketPlayer.PositionRotation packet2 = new CPacketPlayer.PositionRotation(posX, posY, posZ, Phase.mc.player.rotationYaw, Phase.mc.player.rotationPitch, false);
                this.packets.add(packet2);
                Phase.mc.player.connection.sendPacket(packet2);
            }
            if (this.teleportConfirm.getValue() == 4) {
                Phase.mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportIds));
                ++this.teleportIds;
            }
            if (this.fallPacket.getValue()) {
                Phase.mc.player.connection.sendPacket(new CPacketEntityAction(Phase.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            }
            Phase.mc.player.setPosition(posX, posY, posZ);
            boolean bl = this.teleport = !this.teleporter.getValue() || !this.teleport;
            if (event != null) {
                event.setX(0.0);
                event.setY(0.0);
                event.setX(0.0);
            } else {
                Phase.mc.player.motionX = 0.0;
                Phase.mc.player.motionY = 0.0;
                Phase.mc.player.motionZ = 0.0;
            }
        }
    }

    private void doBoundingBox() {
        double[] dirSpeed = this.getMotion(this.teleport ? (double) 0.0225f : (double) 0.0224f);
        Phase.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(Phase.mc.player.posX + dirSpeed[0], Phase.mc.player.posY + (Phase.mc.gameSettings.keyBindJump.isKeyDown() ? (this.teleport ? 0.0625 : 0.0624) : 1.0E-8) - (Phase.mc.gameSettings.keyBindSneak.isKeyDown() ? (this.teleport ? 0.0625 : 0.0624) : 2.0E-8), Phase.mc.player.posZ + dirSpeed[1], Phase.mc.player.rotationYaw, Phase.mc.player.rotationPitch, false));
        Phase.mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(Phase.mc.player.posX, -1337.0, Phase.mc.player.posZ, Phase.mc.player.rotationYaw, Phase.mc.player.rotationPitch, true));
        Phase.mc.player.connection.sendPacket(new CPacketEntityAction(Phase.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        Phase.mc.player.setPosition(Phase.mc.player.posX + dirSpeed[0], Phase.mc.player.posY + (Phase.mc.gameSettings.keyBindJump.isKeyDown() ? (this.teleport ? 0.0625 : 0.0624) : 1.0E-8) - (Phase.mc.gameSettings.keyBindSneak.isKeyDown() ? (this.teleport ? 0.0625 : 0.0624) : 2.0E-8), Phase.mc.player.posZ + dirSpeed[1]);
        this.teleport = !this.teleport;
        Phase.mc.player.motionZ = 0.0;
        Phase.mc.player.motionY = 0.0;
        Phase.mc.player.motionX = 0.0;
        Phase.mc.player.noClip = this.teleport;
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (this.posLook.getValue() && event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            if (Phase.mc.player.isEntityAlive() && Phase.mc.world.isBlockLoaded(new BlockPos(Phase.mc.player.posX, Phase.mc.player.posY, Phase.mc.player.posZ)) && !(Phase.mc.currentScreen instanceof GuiDownloadTerrain)) {
                if (this.teleportIds <= 0) {
                    this.teleportIds = packet.getTeleportId();
                }
                if (this.cancel.getValue() && this.cancelType.getValue()) {
                    packet.yaw = Phase.mc.player.rotationYaw;
                    packet.pitch = Phase.mc.player.rotationPitch;
                    return;
                }
                if (!(!this.cancel.getValue() || this.posLookPackets < this.cancelPacket.getValue() || this.onlyY.getValue() && (Phase.mc.gameSettings.keyBindForward.isKeyDown() || Phase.mc.gameSettings.keyBindRight.isKeyDown() || Phase.mc.gameSettings.keyBindLeft.isKeyDown() || Phase.mc.gameSettings.keyBindBack.isKeyDown()))) {
                    this.posLookPackets = 0;
                    event.setCanceled(true);
                }
                ++this.posLookPackets;
            }
        }
    }

    @Listener
    public void onPacketReceive(PacketEvent.Send event) {
        if (this.scanPackets.getValue() && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packetPlayer = (CPacketPlayer) event.getPacket();
            if (this.packets.contains(packetPlayer)) {
                this.packets.remove(packetPlayer);
            } else {
                event.setCanceled(true);
            }
        }
    }

    private double[] getMotion(double speed) {
        float moveForward = Phase.mc.player.movementInput.moveForward;
        float moveStrafe = Phase.mc.player.movementInput.moveStrafe;
        float rotationYaw = Phase.mc.player.prevRotationYaw + (Phase.mc.player.rotationYaw - Phase.mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += (float) (moveForward > 0.0f ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                rotationYaw += (float) (moveForward > 0.0f ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double posX = (double) moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + (double) moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ = (double) moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - (double) moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[]{posX, posZ};
    }


}

