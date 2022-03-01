/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.MobEffects
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package dev.cuican.staypro.module.modules.movement;



import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.MoveEvent;
import dev.cuican.staypro.event.events.client.UpdateWalkingPlayerEvent;
import dev.cuican.staypro.event.events.network.MoveEvent2;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.module.modules.combat.HoleSnap;
import dev.cuican.staypro.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;
@Parallel
@ModuleInfo(name = "Strafe", category = Category.MOVEMENT, description = "Modifies sprinting")
public class Strafe
extends Module {
    public Setting<String> mode = setting("Mode", "NORMAL",listOf("Strict","NORMAL"));
    private static Strafe INSTANCE = new Strafe();
    private double lastDist;
    private double moveSpeed;
    int stage;

    public Strafe() {
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Strafe getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new Strafe();
        return INSTANCE;
    }

    @Listener
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1 && fullNullCheck()) {
            return;
        }
        this.lastDist = Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX) + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ));
    }

    @Listener
    public void onStrafe(MoveEvent2 event) {
        if (fullNullCheck()) {
            return;
        }
        HoleSnap HoleSnap = (HoleSnap) ModuleManager.getModuleByName("HoleSnap");
        if( HoleSnap.isEnabled()){
            if(HoleSnap.holePos!=null&&!HoleSnap.holePos.equals( new BlockPos.MutableBlockPos(0, -69, 0))){
                return;
            }
        }
        if (mc.player.isInWater()) {
            return;
        }
        if (mc.player.isInLava()) {
            return;
        }


        if (mc.player.onGround) {
            this.stage = 2;
        }
        switch (this.stage) {
            case 0: {
                ++this.stage;
                this.lastDist = 0.0;
                break;
            }
            case 2: {
                double motionY = 0.40123128;
                if (!mc.player.onGround || !mc.gameSettings.keyBindJump.isKeyDown()) {
                    break;
                }
                if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    motionY += (float)(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
                }
                mc.player.motionY = motionY;
                event.setY(mc.player.motionY);
                this.moveSpeed *= this.mode.getValue().equals("NORMAL") ? 1.67 : 2.149;
                break;
            }
            case 3: {
                this.moveSpeed = this.lastDist - (this.mode.getValue().equals("NORMAL") ? 0.6896 : 0.795) * (this.lastDist - this.getBaseMoveSpeed());
                break;
            }
            default: {
                if ((mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && this.stage > 0) {
                    this.stage = mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f ? 1 : 0;
                }
                this.moveSpeed = this.lastDist - this.lastDist / (this.mode.getValue().equals("NORMAL") ? 730.0 : 159.0);
            }
        }
        this.moveSpeed = !mc.gameSettings.keyBindJump.isKeyDown() && mc.player.onGround ? this.getBaseMoveSpeed() : Math.max(this.moveSpeed, this.getBaseMoveSpeed());
        double n = mc.player.movementInput.moveForward;
        double n2 = mc.player.movementInput.moveStrafe;
        double n3 = mc.player.rotationYaw;
        if (n == 0.0 && n2 == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else if (n != 0.0 && n2 != 0.0) {
            n *= Math.sin(0.7853981633974483);
            n2 *= Math.cos(0.7853981633974483);
        }
        double n4 = this.mode.getValue().equals("NORMAL") ? 0.993 : 0.99;
        event.setX((n * this.moveSpeed * -Math.sin(Math.toRadians(n3)) + n2 * this.moveSpeed * Math.cos(Math.toRadians(n3))) * n4);
        event.setZ((n * this.moveSpeed * Math.cos(Math.toRadians(n3)) - n2 * this.moveSpeed * -Math.sin(Math.toRadians(n3))) * n4);
        ++this.stage;
        event.setCanceled(true);
    }

    public double getBaseMoveSpeed() {
        double n = 0.2873;
        if (!mc.player.isPotionActive(MobEffects.SPEED)) {
            return n;
        }
        n *= 1.0 + 0.2 * (double)(Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() + 1);
        return n;
    }


    public String getDisplayInfo() {
        return this.mode.getValue();
    }


}

