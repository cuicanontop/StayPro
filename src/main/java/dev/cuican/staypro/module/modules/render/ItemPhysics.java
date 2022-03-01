package dev.cuican.staypro.module.modules.render;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;


/**
 * @author Madmegsox1
 * @since 03/05/2021
 */
@ModuleInfo(name = "Item Physics", description = "Apply physics to items", category = Category.RENDER)
public class ItemPhysics extends Module {
    public static ItemPhysics INSTANCE;

    public ItemPhysics(){
        INSTANCE = this;
    }
    public final Setting<Double> Scaling = setting("Scaling", 0.5, 0.0, 10.0);

}
