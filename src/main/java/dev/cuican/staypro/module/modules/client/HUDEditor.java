package dev.cuican.staypro.module.modules.client;

import dev.cuican.staypro.client.ConfigManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.gui.StayHUDEditor;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import org.lwjgl.input.Keyboard;

@Parallel
@ModuleInfo(name = "HUDEditor", category = Category.CLIENT, keyCode = Keyboard.KEY_GRAVE, description = "HUDEditor of Stay")
public class HUDEditor extends Module {

    public static HUDEditor instance;

    public HUDEditor() {
        instance = this;
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            if (!(mc.currentScreen instanceof StayHUDEditor)) {
                mc.displayGuiScreen(new StayHUDEditor());
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.currentScreen != null && mc.currentScreen instanceof StayHUDEditor) {
            mc.displayGuiScreen(null);
        }
        ConfigManager.saveAll();
    }

}
