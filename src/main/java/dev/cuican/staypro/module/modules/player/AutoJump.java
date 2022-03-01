package dev.cuican.staypro.module.modules.player;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;

@Parallel(runnable = true)
@ModuleInfo(name = "AutoJump", category = Category.PLAYER, description = "Automatically jump")
public class AutoJump extends Module {

    @Override
    public void onTick() {
        if (mc.player == null) return;
        if (mc.player.isInWater() || mc.player.isInLava()) mc.player.motionY = 0.1;
        else if (mc.player.onGround) mc.player.jump();
    }

}
