
package dev.cuican.staypro.module.modules.movement;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;

@ModuleInfo(name = "EntityControl", category = Category.MOVEMENT,description = "EntityControl")
public class EntityControl
extends Module {
    public static EntityControl INSTANCE;

    public EntityControl() {
        INSTANCE = this;
    }
}

