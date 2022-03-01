package dev.cuican.staypro.module.modules.movement;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;

@Parallel(runnable = true)
@ModuleInfo(name = "Sprint", category = Category.MOVEMENT, description = "Automatically sprint")
public class Sprint extends Module {

    @Override
    public void onRenderTick() {
        if (mc.player == null) return;
        mc.player.setSprinting(!mc.player.collidedHorizontally && mc.player.moveForward > 0);
    }

}
