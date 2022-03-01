package dev.cuican.staypro.module.modules.misc;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
@ModuleInfo(name = "MultiTask", category = Category.MISC, description = "Allows you to eat while mining.")
public class MultiTask extends Module {
    private static MultiTask INSTANCE = new MultiTask();


    public static MultiTask getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MultiTask();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}

