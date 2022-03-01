package dev.cuican.staypro.module.modules.movement;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.MoveEvent;
import dev.cuican.staypro.event.events.client.PacketEvents;
import dev.cuican.staypro.event.events.client.UpdateWalkingPlayerEvent;
import dev.cuican.staypro.event.events.network.MotionUpdateMultiplierEvent;
import dev.cuican.staypro.event.events.network.MoveEvent2;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.RandomUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.util.MovementInput;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@ModuleInfo(name = "Speed", category = Category.MOVEMENT)
public class Speed extends Module {
    public static Speed INSTANCE = new Speed();
    public Setting<String> mode = setting("Mode", "BYPASS",listOf(
            "BYPASS",
            "NCPHOP",
            "MODE2B2T",
            "MINIHOP",
            "SLOWHOP",
            "LOWHOP",
            "HYPIXELBHOP"
    ));
    public Setting<Boolean> boost = setting("Boost", false).whenAtMode(mode,"BYPASS");
    public Setting<Integer> boostFactor = setting("BoostFactor", 3, 1, 10).whenTrue(boost).whenAtMode(mode,"BYPASS");
    public Setting<Boolean> strict = setting("Strict", false).whenAtMode(mode,"NCPHOP");
    public Setting<Double> multispeed = setting("OnGroundSpeed", 1.733, 0, 10).whenAtMode(mode,"MINIHOP");
    public Setting<Double> firstMinus = setting("OnAirSpeed", 1.14, 0, 10).whenAtMode(mode,"MINIHOP");
    public Setting<Boolean> lagbackCheck = setting("LagBackCheck", false);
    public Setting<Boolean> potion = setting("Potion", true);
    public Setting<Boolean> step = setting("SetStep", true);
    public Setting<Boolean> SpeedInWater = setting("SpeedInWater", true);
    public Setting<Boolean> useTimer = setting("UseTimer", true);
    public Setting<Float> stepHeight = setting("StepHeight", 2f, 0, 3);
    public double lastDist;
    public int stage;
    public int level;
    public double moveSpeed;
    public boolean speedTick;

    public Speed() {
        stage = 1;
        level = 1;
    }

    public static double round(double n, int n2) {
        if (n2 < 0) {
            throw new IllegalArgumentException();
        }
        return new BigDecimal(n).setScale(n2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double getDirection() {
        float var1 = mc.player.rotationYaw;
        if (mc.player.moveForward < 0.0F)
            var1 += 180.0F;
        float forward = 1.0F;
        if (mc.player.moveForward < 0.0F) {
            forward = -0.5F;
        } else if (mc.player.moveForward > 0.0F) {
            forward = 0.5F;
        }
        if (mc.player.moveStrafing > 0.0F)
            var1 -= 90.0F * forward;
        if (mc.player.moveStrafing < 0.0F)
            var1 += 90.0F * forward;
        var1 *= 0.017453292F;
        return var1;
    }
    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (fullNullCheck()) {
            return;
        }

        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            if (lagbackCheck.getValue()) {
                moveSpeed = getBaseMoveSpeed();
                packet.yaw = mc.player.rotationYaw;
                packet.pitch = mc.player.rotationPitch;
                ChatUtil.sendNoSpamMessage("Rubberband Detected! Position Adjusted!");
                stage = 0;
            }
            lastDist = 0.0;
            moveSpeed = getBaseMoveSpeed();
            stage = 2;
        }
    }

    @Override
    public String getModuleInfo() {
        if (mode.getValue().equals("SLOWHOP")) {
            return "SlowHop";
        }
        if (mode.getValue().equals("MODE2B2T")) {
            return "2b2t";
        }
        if (mode.getValue().equals("NCPHOP")) {
            return "NCPHop";
        }
        if (mode.getValue().equals("BYPASS")) {
            return "Bypass";
        }
        if (mode.getValue().equals("LOWHOP")) {
            return "LowHop";
        }
        if (mode.getValue().equals("MINIHOP")) {
            return "MiniHop";
        }
        if (mode.getValue().equals("HYPIXELBHOP")) {
            return "HypixelBHOP";
        }
        return null;
    }

//
//    @Listener
//    private final void onMotionFactor(MotionUpdateMultiplierEvent event) {
//        if (fullNullCheck()) return;
//        if(isDisabled()) return;
//        if(mc.player.hurtTime>=1){
//        event.setFactor(8);
//
//        }
//    }

    public double getBaseMoveSpeed() {
        double n = 0.2873;
        if (mc.player.isPotionActive(MobEffects.SPEED) && potion.getValue()) {
            n *= 1.0 + 0.2 * (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() + 1);
        }
        return n;
    }

    @Override
    public void onTick() {
        if (useTimer.getValue()) {
            mc.timer.tickLength = 45.955883f;
        } else {
            mc.timer.tickLength = 50.0f;
        }
    }

    @Listener
    public void lambda$new$6(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1) {
            lastDist = Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX) + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ));
        }
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            disable();
            return;
        }
        if ((mode.getValue().equals("LOWHOP") || mode.getValue() .equals("MINIHOP")) && mc.player.onGround) {
            mc.player.stepHeight = stepHeight.getValue();
        }
        moveSpeed = getBaseMoveSpeed();
//        if (ModuleManager.getModuleByName("LongJump").isEnabled()) {
//            ModuleManager.getModuleByName("LongJump").disable();
//        }
    }

    @Listener
    public void move(MoveEvent2 event) {
        if (!SpeedInWater.getValue()) {
            if (shouldReturn()) {
                return;
            }
        }
        if (mode.getValue().equals("LOWHOP") && mc.player.onGround) {
            mc.player.stepHeight = stepHeight.getValue();
        } else {
            mc.player.stepHeight = 0.0f;
        }
        if (mode.getValue() .equals("SLOWHOP")|| mode.getValue().equals("MODE2B2T") || mode.getValue().equals("NCPHOP")) {
            switch (stage) {
                case 0: {
                    ++stage;
                    lastDist = 0.0;
                    break;
                }
                case 2: {
                    double motionY = 0.40123128;
                    if ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) && mc.player.onGround) {
                        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                            motionY += (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier() + 1) * 0.1f;
                        }
                        event.setY(mc.player.motionY = motionY);
                        moveSpeed *= ((mode.getValue() .equals("SLOWHOP")) ? 1.67 : 2.149);
                        break;
                    }
                    break;
                }
                case 3: {
                    double n = strict.getValue() ? 0.771 : 0.77;
                    if (mode.getValue() .equals("MODE2B2T")) {
                        n = 0.795;
                    }
                    if (mode.getValue() .equals("SLOWHOP")) {
                        n = 0.6896;
                    }
                    moveSpeed = lastDist - n * (lastDist - getBaseMoveSpeed());
                    break;
                }
                default: {
                    if ((mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && stage > 0) {
                        stage = ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) ? 1 : 0);
                    }
                    moveSpeed = lastDist - lastDist / ((mode.getValue() .equals("SLOWHOP")) ? 730.0 : 159.0);
                    break;
                }
            }
            moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
            double n2 = mc.player.movementInput.moveForward;
            double n3 = mc.player.movementInput.moveStrafe;
            double n4 = mc.player.rotationYaw;
            if (n2 == 0.0 && n3 == 0.0) {
                event.setX(0.0);
                event.setZ(0.0);
            } else if (n2 != 0.0 && n3 != 0.0) {
                n2 *= Math.sin(0.7853981633974483);
                n3 *= Math.cos(0.7853981633974483);
            }
            double n5 = (mode.getValue().equals("SLOWHOP")) ? 0.993 : 0.99;
            event.setX((n2 * moveSpeed * -Math.sin(Math.toRadians(n4)) + n3 * moveSpeed * Math.cos(Math.toRadians(n4))) * n5);
            event.setZ((n2 * moveSpeed * Math.cos(Math.toRadians(n4)) - n3 * moveSpeed * -Math.sin(Math.toRadians(n4))) * n5);
            ++stage;
        } else if (mode.getValue().equals("LOWHOP")) {
            MovementInput movementInput = mc.player.movementInput;
            float moveForward = movementInput.moveForward;
            float moveStrafe = movementInput.moveStrafe;
            float rotationYaw = mc.player.rotationYaw;
            if (moveForward == 0.0f && moveStrafe == 0.0f) {
                event.X = 0.0;
                event.Z = 0.0;
            } else if (moveForward != 0.0f) {
                if (moveStrafe >= 1.0f) {
                    rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
                    moveStrafe = 0.0f;
                } else if (moveStrafe <= -1.0f) {
                    rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
                    moveStrafe = 0.0f;
                }
                if (moveForward > 0.0f) {
                    moveForward = 1.0f;
                } else if (moveForward < 0.0f) {
                    moveForward = -1.0f;
                }
            }
            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0f));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0f));
            if (!shouldReturn() && !mc.player.isInWater() && (moveForward != 0.0f || moveStrafe != 0.0f)) {
                if (mc.player.onGround && (moveForward != 0.0f || moveStrafe != 0.0f)) {
                    level = 2;
                }
                if (round(mc.player.posY - (int) mc.player.posY, 3) == round(0.4, 3)) {
                    EntityPlayerSP player = mc.player;
                    double n6 = 0.31;
                    event.Y = n6;
                    player.motionY = n6;
                } else if (round(mc.player.posY - (int) mc.player.posY, 3) == round(0.71, 3)) {
                    EntityPlayerSP player2 = mc.player;
                    double n7 = 0.04;
                    event.Y = n7;
                    player2.motionY = n7;
                } else if (round(mc.player.posY - (int) mc.player.posY, 3) == round(0.75, 3)) {
                    EntityPlayerSP player3 = mc.player;
                    double n8 = -0.2;
                    event.Y = n8;
                    player3.motionY = n8;
                } else if (round(mc.player.posY - (int) mc.player.posY, 3) == round(0.55, 3)) {
                    EntityPlayerSP player4 = mc.player;
                    double n9 = -0.14;
                    event.Y = n9;
                    player4.motionY = n9;
                } else if (round(mc.player.posY - (int) mc.player.posY, 3) == round(0.41, 3)) {
                    EntityPlayerSP player5 = mc.player;
                    double n10 = -0.2;
                    event.Y = n10;
                    player5.motionY = n10;
                }
                if (level == -1) {
                    event.X *= 0.3;
                    event.Z *= 0.3;
                }
                if (level != 1 || (mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f)) {
                    if (level == 2) {
                        if (mc.player.collidedVertically) {
                            event.Y = 0.4;
                        }
                        moveSpeed *= 1.7;
                    } else if (level == 3) {
                        moveSpeed = lastDist - 0.76 * (lastDist - getBaseMoveSpeed());
                    } else {
                        if ((mc.world.getCollisionBoxes(mc.player, mc.player.boundingBox.offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && level > 0) {
                            level = ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) ? 1 : 0);
                        }
                        moveSpeed = lastDist - lastDist / 159.0;
                    }
                } else {
                    moveSpeed = 1.35 * getBaseMoveSpeed();
                }
                moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
                event.X = moveForward * moveSpeed * cos + moveStrafe * moveSpeed * sin;
                event.Z = moveForward * moveSpeed * sin - moveStrafe * moveSpeed * cos;
                if (moveForward == 0.0f && moveStrafe == 0.0f) {
                    event.X = 0.0;
                    event.Z = 0.0;
                }
                ++level;
            } else {
                level = -8;
            }
        }
        /*
        if (mode.getValue() == Mode.STRAFE) {
            if (EntityUtil.isMoving()) {
                event.X *= 1.0999999809265137;
                event.Z *= 1.0999999809265137;
            }
            if (mc.player.onGround && EntityUtil.isMoving()) {
                stage = 2;
            }
            if (MathUtil.round(0.0, 3) == MathUtil.round(0.138, 3)) {
                mc.player.motionY -= 0.08;
                event.Y -= 0.09316090325960147;
                mc.player.posY -= 0.09316090325960147;
            }
            if (stage == 1 && mc.player.collidedVertically && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f)) {
                moveSpeed = 1.35 * getBaseMoveSpeed() - 0.01;
            } else if (stage == 2 && EntityUtil.isMoving() && mc.player.collidedVertically && !EntityUtil.isInWater(mc.player) && mc.player.onGround) {
                mc.player.jump();
                mc.player.motionY = 0.4;
                event.Y = 0.4;
                mc.player.posY -= 0.09316090325960147;
            } else if (stage == 3) {
                double difference = 0.66 * (lastDist - getBaseMoveSpeed());
                moveSpeed = lastDist - difference;
                //moveSpeed = 0.3;
            } else {
                if ((mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && stage > 0) {
                    if (EntityUtil.isMoving()) {
                        stage = 1;
                    } else {
                        stage = 0;
                    }
                }
                moveSpeed = lastDist - lastDist / 109.0;
            }
            moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
            long ncpTimer = 0L;
            if (System.currentTimeMillis() - ncpTimer > 2500L) {
                ncpTimer = System.currentTimeMillis();
                if (System.currentTimeMillis() - ncpTimer > 1250L) {
                    moveSpeed = Math.max(moveSpeed, 0.80);
                } else {
                    moveSpeed = Math.max(moveSpeed, 0.76);
                }
            }
            if (mc.player.hurtTime > 7 && zoomBoost.getValue() > 0.0) {
                moveSpeed = Math.max(moveSpeed + (zoomBoost.getValue() / 10f), getBaseMoveSpeed());
            }
            MovementInput movementInput = mc.player.movementInput;
            float moveForward = movementInput.moveForward;
            float moveStrafe = movementInput.moveStrafe;
            float rotationYaw = mc.player.rotationYaw;
            if (moveForward == 0.0f && moveStrafe == 0.0f) {
                event.X = 0.0;
                event.Z = 0.0;
            } else if (moveForward != 0.0f) {
                if (moveStrafe >= 1.0f) {
                    rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
                    moveStrafe = 0.0f;
                } else if (moveStrafe <= -1.0f) {
                    rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
                    moveStrafe = 0.0f;
                }
                if (moveForward > 0.0f) {
                    moveForward = 1.0f;
                } else if (moveForward < 0.0f) {
                    moveForward = -1.0f;
                }
            }
            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0f));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0f));
            //if (EntityUtil.isInWater(mc.player)) moveSpeed = 0.1;
            event.X = moveForward * moveSpeed * cos + moveStrafe * moveSpeed * sin;
            event.Z = moveForward * moveSpeed * sin - moveStrafe * moveSpeed * cos;
            //EntityUtil.setMoveMotionNew(event, moveSpeed);
            //mc.player.jumpMovementFactor = 0.029F;
            if (EntityUtil.isMoving()) {
                ++stage;
            }
        }
         */
        else if (this.mode.getValue().equals("BYPASS")) {
            if (!SpeedInWater.getValue()) {
                if (this.shouldReturn()) {
                    return;
                }
            }
            if (mc.player.onGround) {
                this.level = 2;
            }
            if (this.step.getValue()) {
                mc.player.stepHeight = 0.6f;
            }
            if (round(mc.player.posY - (int) mc.player.posY, 3) == round(0.138, 3)) {
                mc.player.motionY -= 0.07;
                event.Y -= 0.08316090325960147;
                mc.player.posY -= 0.08316090325960147;
            }
            if (this.level != 1 || (mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f)) {
                if (this.level == 2) {
                    this.level = 3;
                    if (EntityUtil.isMoving()) {
                        mc.player.motionY = 0.4;
                        event.Y = 0.4;
                        this.moveSpeed *= 1.682;
                    }
                } else if (this.level == 3) {
                    this.level = 4;
                    this.moveSpeed = this.lastDist - 0.6556 * (this.lastDist - this.getBaseMoveSpeed() + 0.015);
                } else {
                    if (mc.player.onGround && (mc.world.getCollisionBoxes(mc.player, mc.player.boundingBox.offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically)) {
                        this.level = 1;
                    }
                    this.moveSpeed = this.lastDist - this.lastDist / 101.0;
                }
            } else {
                this.level = 2;
                this.moveSpeed = 1.397 * this.getBaseMoveSpeed();
            }
            if (boost.getValue()) {
                if (boostFactor.getValue() >= 1) {
                    if (mc.player.hurtTime >= 1 && mc.player.hurtTime <= 2) {
                        moveSpeed = Math.max(moveSpeed + getBaseMoveSpeed(), moveSpeed + getBaseMoveSpeed() + 0.0034);
                    }
                }
                if (boostFactor.getValue() >= 2) {
                    if (mc.player.hurtTime >= 2 && mc.player.hurtTime <= 3) {
                        moveSpeed = Math.max(moveSpeed + getBaseMoveSpeed(), moveSpeed + getBaseMoveSpeed() + 0.0047);
                    }
                }
                if (boostFactor.getValue() >= 3) {
                    if (mc.player.hurtTime >= 3 && mc.player.hurtTime <= 4) {
                        moveSpeed = Math.max(moveSpeed + getBaseMoveSpeed(), moveSpeed + getBaseMoveSpeed() + 0.00556);
                    }
                }
                if (boostFactor.getValue() >= 4) {
                    if (mc.player.hurtTime >= 4 && mc.player.hurtTime <= 5) {
                        moveSpeed = Math.max(moveSpeed + getBaseMoveSpeed(), moveSpeed + getBaseMoveSpeed() + 0.007343);
                    }
                }
                if (boostFactor.getValue() >= 5) {
                    if (mc.player.hurtTime >= 5 && mc.player.hurtTime <= 6) {
                        moveSpeed = Math.max(moveSpeed + getBaseMoveSpeed(), moveSpeed + getBaseMoveSpeed() + 0.008572);
                    }
                }
                if (boostFactor.getValue() >= 6) {
                    if (mc.player.hurtTime >= 6 && mc.player.hurtTime <= 7) {
                        moveSpeed = Math.max(moveSpeed + getBaseMoveSpeed(), moveSpeed + getBaseMoveSpeed() + 0.009643);
                    }
                }
                if (boostFactor.getValue() >= 7) {
                    if (mc.player.hurtTime >= 7 && mc.player.hurtTime <= 8) {
                        moveSpeed = Math.max(moveSpeed + getBaseMoveSpeed(), moveSpeed + getBaseMoveSpeed() + 0.01);
                    }
                }
                if (boostFactor.getValue() >= 8) {
                    if (mc.player.hurtTime >= 8 && mc.player.hurtTime <= 9) {
                        moveSpeed = Math.max(moveSpeed + getBaseMoveSpeed(), moveSpeed + getBaseMoveSpeed() + 0.017);
                    }
                }
                if (boostFactor.getValue() >= 9) {
                    if (mc.player.hurtTime >= 9 && mc.player.hurtTime <= 10) {
                        moveSpeed = Math.max(moveSpeed + getBaseMoveSpeed(), moveSpeed + getBaseMoveSpeed() + 0.024);
                    }
                }
                if (boostFactor.getValue() == 10) {
                    if (mc.player.hurtTime >= 10) {
                        moveSpeed = Math.max(moveSpeed + getBaseMoveSpeed(), moveSpeed + getBaseMoveSpeed() + 0.031);
                    }
                }
            }
            this.moveSpeed = Math.max(this.moveSpeed, this.getBaseMoveSpeed());
            MovementInput movementInput2 = mc.player.movementInput;
            float moveForward2 = movementInput2.moveForward;
            float moveStrafe2 = movementInput2.moveStrafe;
            float rotationYaw2 = mc.player.rotationYaw;
            if (moveForward2 != 0.0f) {
                if (moveStrafe2 > 0.0f) {
                    rotationYaw2 += ((moveForward2 > 0.0f) ? -45 : 45);
                    moveStrafe2 = 0.0f;
                } else if (moveStrafe2 < 0.0f) {
                    rotationYaw2 += ((moveForward2 > 0.0f) ? 45 : -45);
                    moveStrafe2 = 0.0f;
                }
                if (moveForward2 > 0.0f) {
                    moveForward2 = 1f;
                } else if (moveForward2 < 0.0f) {
                    moveForward2 = -1f;
                }
            }
            double cos2 = Math.cos(Math.toRadians(rotationYaw2 + 90.0f));
            double sin2 = Math.sin(Math.toRadians(rotationYaw2 + 90.0f));
            event.X = moveForward2 * this.moveSpeed * cos2 + moveStrafe2 * this.moveSpeed * sin2;
            event.Z = moveForward2 * this.moveSpeed * sin2 - moveStrafe2 * this.moveSpeed * cos2;
            mc.player.stepHeight = 0.6f;
            if (moveForward2 == 0.0f && moveStrafe2 == 0.0f) {
                event.X = 0.0;
                event.Z = 0.0;
            }
        } else if (mode.getValue().equals("HYPIXELBHOP")) {
            switch (stage) {
                case 0: {
                    ++stage;
                    lastDist = 0.0;
                    break;
                }
                case 2: {
                    double motionY = 0.40123128;
                    if ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) && mc.player.onGround) {
                        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                            motionY += (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier() + 1) * 0.1f;
                        }
                        event.setY(mc.player.motionY = motionY);
                        moveSpeed *= 1.398;
                        break;
                    }
                    break;
                }
                case 3: {
                    moveSpeed = lastDist - 0.5896 * (lastDist - getBaseMoveSpeed() - 0.01);
                    stage = 0;
                    break;
                }
                default: {
                    if ((mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && stage > 0) {
                        stage = ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) ? 1 : 0);
                    }
                    moveSpeed = lastDist - lastDist / 159.0;
                    break;
                }
            }
            moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed() - RandomUtil.nextDouble(0.005, 0.01));
            double n2 = mc.player.movementInput.moveForward;
            double n3 = mc.player.movementInput.moveStrafe;
            double n4 = mc.player.rotationYaw;
            if (n2 == 0.0 && n3 == 0.0) {
                event.setX(0.0);
                event.setZ(0.0);
            } else if (n2 != 0.0 && n3 != 0.0) {
                n2 *= Math.sin(0.7853981633974483);
                n3 *= Math.cos(0.7853981633974483);
            }
            double n5 = 0.98;
            event.setX((n2 * moveSpeed * -Math.sin(Math.toRadians(n4)) + n3 * moveSpeed * Math.cos(Math.toRadians(n4))) * n5);
            event.setZ((n2 * moveSpeed * Math.cos(Math.toRadians(n4)) - n3 * moveSpeed * -Math.sin(Math.toRadians(n4))) * n5);
            ++stage;
        } else if (mode.getValue().equals("MINIHOP")) {
            mc.player.stepHeight = 0.6f;
            MovementInput movementInput3 = mc.player.movementInput;
            float moveForward3 = movementInput3.moveForward;
            float moveStrafe3 = movementInput3.moveStrafe;
            float rotationYaw3 = mc.player.rotationYaw;
            if (moveForward3 == 0.0f && moveStrafe3 == 0.0f) {
                event.X = 0.0;
                event.Z = 0.0;
            } else if (moveForward3 != 0.0f) {
                if (moveStrafe3 >= 1.0f) {
                    rotationYaw3 += ((moveForward3 > 0.0f) ? -45 : 45);
                    moveStrafe3 = 0.0f;
                } else if (moveStrafe3 <= -1.0f) {
                    rotationYaw3 += ((moveForward3 > 0.0f) ? 45 : -45);
                    moveStrafe3 = 0.0f;
                }
                if (moveForward3 > 0.0f) {
                    moveForward3 = 1.0f;
                } else if (moveForward3 < 0.0f) {
                    moveForward3 = -1.0f;
                }
            }
            double cos3 = Math.cos(Math.toRadians(rotationYaw3 + 90.0f));
            double sin3 = Math.sin(Math.toRadians(rotationYaw3 + 90.0f));
            if (!shouldReturn() && !mc.player.isInWater() && (moveForward3 != 0.0f || moveStrafe3 != 0.0f)) {
                if (mc.player.onGround && (moveForward3 != 0.0f || moveStrafe3 != 0.0f)) {
                    level = 2;
                }
                if (level == -1) {
                    event.X *= 0.3;
                    event.Z *= 0.3;
                }
                if (level != 1 || (mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f)) {
                    if (level == 2) {
                        event.Y = 0.4;
                        moveSpeed = getBaseMoveSpeed() * multispeed.getValue();
                    } else if (level == 3) {
                        moveSpeed = getBaseMoveSpeed() * firstMinus.getValue();
                        EntityPlayerSP player8 = mc.player;
                        double n11 = -0.4;
                        event.Y = n11;
                        player8.motionY = n11;
                    } else {
                        if ((mc.world.getCollisionBoxes(mc.player, mc.player.boundingBox.offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && level > 0) {
                            level = ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) ? 1 : 0);
                            mc.player.stepHeight = stepHeight.getValue();
                        }
                        moveSpeed = lastDist - lastDist / 159.0;
                    }
                }
                moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
                event.X = moveForward3 * moveSpeed * cos3 + moveStrafe3 * moveSpeed * sin3;
                event.Z = moveForward3 * moveSpeed * sin3 - moveStrafe3 * moveSpeed * cos3;
                if (moveForward3 == 0.0f && moveStrafe3 == 0.0f) {
                    event.X = 0.0;
                    event.Z = 0.0;
                }
                ++level;
                speedTick = !speedTick;
            } else {
                level = -8;
            }
        }
        //event.cancel();
    }

    public boolean shouldReturn() {
        return mc.player.isInLava() || mc.player.isInWater() || isDisabled() || mc.player.isInWeb;
    }

    @Override
    public void onDisable() {
        moveSpeed = 0.0;
        stage = 2;
        if (mc.player != null) {
            mc.player.stepHeight = 0.6f;
            mc.timer.tickLength = 50.0f;
        }
    }


}
