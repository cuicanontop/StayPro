package dev.cuican.staypro.module.modules.render;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;

@ModuleInfo(name = "Camera Clip", description = "f5 mode", category = Category.RENDER)
public class CameraClip extends Module {

    public static CameraClip INSTANCE;

    public CameraClip() {
        INSTANCE = this;
    }


    public final Setting<Double> distance = setting("Distance", 10.0, -10.0, 50.0);
}
