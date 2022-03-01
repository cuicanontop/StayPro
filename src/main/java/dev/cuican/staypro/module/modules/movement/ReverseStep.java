package dev.cuican.staypro.module.modules.movement;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;

@ModuleInfo(name = "ReverseStep", category = Category.MOVEMENT, description = "ReverseStep")
public class ReverseStep extends Module {

    public Setting<Boolean> FallSpeed = setting("UseFallSpeed",true);
    public Setting<Integer> FallingSpeed = setting("FallSpeed",3,1,10);
    private final Setting<Double> height = setting("Height",3,0.5,3).whenFalse(FallSpeed);

    @Override
    public void onTick() {
        if (fullNullCheck() || mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || mc.gameSettings.keyBindJump.isKeyDown()) {
            return;
        }
        if (mc.player != null && mc.player.onGround && !mc.player.isInWater() && !mc.player.isOnLadder()) {
            if (FallSpeed.getValue()) {
                mc.player.motionY -= FallingSpeed.getValue();
            } else {
                for (double y = 0.0; y < this.height.getValue() + 0.5; y += 0.01) {
                    if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                        mc.player.motionY = -15.0;
                        break;
                    }
                }
            }
        }
    }
}
