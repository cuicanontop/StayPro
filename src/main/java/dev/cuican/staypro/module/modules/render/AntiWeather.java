package dev.cuican.staypro.module.modules.render;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;


/**
 * Created by 086 on 8/04/2018.
 */
@ModuleInfo(name = "AntiWeather", category = Category.RENDER, description = "ARemoves rain from your world")
public class AntiWeather extends Module {


    @Override
    public void onTick() {
        if (isDisabled()) {
            return;
        }
        if (mc.world.isRaining()) {
            mc.world.setRainStrength(0);
        }
    }
}
