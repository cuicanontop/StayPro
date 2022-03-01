package dev.cuican.staypro.module.modules.movement;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.concurrent.utils.Timer;
import dev.cuican.staypro.event.events.client.PacketEvents;
import dev.cuican.staypro.event.events.network.EventPlayerTravel;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.MathUtil;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

@ModuleInfo(name = "ElytraFly", description = "Allows you to fly with elytra on 2b2t", category = Category.MOVEMENT)
public class ElytraPlus extends Module {

    public final Setting<?> mode = setting("Mode", "Superior",listOf( "Superior", "Packet"));
    public final Setting<Float> speed = setting("Speed", 18f, 0, 50);
    public final Setting<Float> DownSpeed = setting("DownSpeed", 1.8f, 0, 10);
    public final Setting<Float> GlideSpeed = setting("GlideSpeed", 0.0001f, 0, 10);
    public final Setting<Float> UpSpeed = setting("UpSpeed", 5f, 0, 10);
    public final Setting<Boolean> Accelerate = setting("Accelerate", true);
    public final Setting<Integer> vAccelerationTimer = setting("AccTime", 1000, 0, 10000);
    public final Setting<Float> RotationPitch = setting("RotationPitch", 45f, 0, 90);
    public final Setting<Boolean> CancelInWater = setting("CancelInWater", true);
    public final Setting<Integer> CancelAtHeight = setting("CancelHeight", 0, 0, 10);
    public final Setting<Boolean> InstantFly = setting("FastBoost", true);
    public final Setting<Boolean> onEnableEquipElytra = setting("AutoEnableWhileElytra", false);
    public final Setting<Boolean> PitchSpoof = setting("PitchSpoof", false);

    private final Timer AccelerationTimer = new Timer();
    private final Timer AccelerationResetTimer = new Timer();
    private final Timer InstantFlyTimer = new Timer();
    private boolean SendMessage = false;
    private int ElytraSlot = -1;

    @Listener
    public void PacketEvent(PacketEvents.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && PitchSpoof.getValue()) {
            if (!mc.player.isElytraFlying()) return;
            if (event.getPacket() instanceof CPacketPlayer.PositionRotation && PitchSpoof.getValue()) {
                CPacketPlayer.PositionRotation rotation = event.getPacket();
                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(rotation.x, rotation.y, rotation.z, rotation.onGround));
                event.setCanceled(true);
            } else if (event.getPacket() instanceof CPacketPlayer.Rotation && PitchSpoof.getValue()) {
                event.setCanceled(true);
            }
        }
    }

    @Listener
    public void Travel(EventPlayerTravel event) {
        if (mc.player == null)
            return;
        if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA)
            return;
        if (!mc.player.isElytraFlying()) {
            if (!mc.player.onGround && InstantFly.getValue()) {
                if (!InstantFlyTimer.passed(500))
                    return;
                InstantFlyTimer.reset();
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_FALL_FLYING));
            }
            return;
        }
        if(mode.getValue().equals("Packet")){
            HandleNormalModeElytra(event);
        }
        if(mode.getValue().equals("Superior")){
            HandleImmediateModeElytra(event);
        }
    }

    @Override
    public String getModuleInfo() {
        return  mode.getValue().toString() ;
    }

    @Override
    public void onEnable() {
        ElytraSlot = -1;
        if (onEnableEquipElytra.getValue()) {
            if (mc.player != null && mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
                for (int i = 0; i < 44; ++i) {
                    ItemStack stacktemp = mc.player.inventory.getStackInSlot(i);
                    if (stacktemp.isEmpty() || stacktemp.getItem() != Items.ELYTRA)
                        continue;
                    ElytraSlot = i;
                    break;
                }
                if (ElytraSlot != -1) {
                    boolean l_HasArmorAtChest = mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.AIR;
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, ElytraSlot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, mc.player);
                    if (l_HasArmorAtChest)
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, ElytraSlot, 0, ClickType.PICKUP, mc.player);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        if (ElytraSlot != -1) {
            boolean l_HasItem = !mc.player.inventory.getStackInSlot(ElytraSlot).isEmpty() || mc.player.inventory.getStackInSlot(ElytraSlot).getItem() != Items.AIR;
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, ElytraSlot, 0, ClickType.PICKUP, mc.player);
            if (l_HasItem) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, mc.player);
            }
        }
    }

    public void HandleNormalModeElytra(EventPlayerTravel p_Travel) {
        double l_YHeight = mc.player.posY;
        if (l_YHeight <= CancelAtHeight.getValue()) {
            if (!SendMessage) {
                ChatUtil.sendNoSpamMessage("WARNING, you must scaffold up or use fireworks, as YHeight <= CancelAtHeight!");
                SendMessage = true;
            }
            return;
        }
        boolean isMoveKeyDown = mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()
                || mc.gameSettings.keyBindBack.isKeyDown();
        boolean l_CancelInWater = !mc.player.isInWater() && !mc.player.isInLava() && CancelInWater.getValue();
        if (!isMoveKeyDown) {
            AccelerationTimer.resetTimeSkipTo(-vAccelerationTimer.getValue());
        } else if ((mc.player.rotationPitch <= RotationPitch.getValue()) && l_CancelInWater) {
            if (Accelerate.getValue()) {
                if (AccelerationTimer.passed(vAccelerationTimer.getValue())) {
                    Accelerate();
                    return;
                }
            }
            return;
        }
        p_Travel.setCanceled(true);
        Accelerate();
    }

    public void HandleImmediateModeElytra(EventPlayerTravel p_Travel) {
        p_Travel.setCanceled(true);
        boolean moveForward = mc.gameSettings.keyBindForward.isKeyDown();
        boolean moveBackward = mc.gameSettings.keyBindBack.isKeyDown();
        boolean moveLeft = mc.gameSettings.keyBindLeft.isKeyDown();
        boolean moveRight = mc.gameSettings.keyBindRight.isKeyDown();
        boolean moveUp = mc.gameSettings.keyBindJump.isKeyDown();
        boolean moveDown = mc.gameSettings.keyBindSneak.isKeyDown();
        float moveForwardFactor = moveForward ? 1.0f : (float) (moveBackward ? -1 : 0);
        float yawDeg = mc.player.rotationYaw;

        if (moveLeft && (moveForward || moveBackward)) {
            yawDeg -= 40.0f * moveForwardFactor;
        } else if (moveRight && (moveForward || moveBackward)) {
            yawDeg += 40.0f * moveForwardFactor;
        } else if (moveLeft) {
            yawDeg -= 90.0f;
        } else if (moveRight) {
            yawDeg += 90.0f;
        }
        if (moveBackward)
            yawDeg -= 180.0f;

        float yaw = (float) Math.toRadians(yawDeg);
        double motionAmount = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
        if (moveUp || moveForward || moveBackward || moveLeft || moveRight) {
            if ((moveUp) && motionAmount > 1.0) {
                if (mc.player.motionX == 0.0 && mc.player.motionZ == 0.0) {
                    mc.player.motionY = UpSpeed.getValue();
                } else {
                    double calcMotionDiff = motionAmount * 0.008;
                    mc.player.motionY += calcMotionDiff * 3.2;
                    mc.player.motionX -= (double) (-MathHelper.sin(yaw)) * calcMotionDiff;
                    mc.player.motionZ -= (double) MathHelper.cos(yaw) * calcMotionDiff;
                    mc.player.motionX *= 0.99f;
                    mc.player.motionY *= 0.98f;
                    mc.player.motionZ *= 0.99f;
                }
            } else { /* runs when pressing wasd */
                mc.player.motionX = (double) (-MathHelper.sin(yaw)) * (speed.getValue() / 10);
                mc.player.motionY = -GlideSpeed.getValue();
                mc.player.motionZ = (double) MathHelper.cos(yaw) * (speed.getValue() / 10);
            }
        } else { /* Stop moving if no inputs are pressed */
            mc.player.motionX = 0.0;
            mc.player.motionY = 0.0;
            mc.player.motionZ = 0.0;
        }
        if (moveDown) {
            mc.player.motionY = -DownSpeed.getValue();
        }
    }

    public void Accelerate() {
        if (AccelerationResetTimer.passed(vAccelerationTimer.getValue())) {
            AccelerationResetTimer.reset();
            AccelerationTimer.reset();
            SendMessage = false;
        }

        float speedacc = this.speed.getValue() / 10f;

        final double[] dir = MathUtil.directionSpeed(speedacc);

        mc.player.motionY = -GlideSpeed.getValue();

        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
            mc.player.motionX -= (mc.player.motionX * (Math.abs(mc.player.rotationPitch) + 90) / 90) - mc.player.motionX;
            mc.player.motionZ -= (mc.player.motionZ * (Math.abs(mc.player.rotationPitch) + 90) / 90) - mc.player.motionZ;
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }

        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY = -DownSpeed.getValue();
        }

        mc.player.prevLimbSwingAmount = 0;
        mc.player.limbSwingAmount = 0;
        mc.player.limbSwing = 0;
    }



}
