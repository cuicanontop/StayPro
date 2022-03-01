package dev.cuican.staypro.module.modules.client;

import dev.cuican.staypro.client.ConfigManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.gui.StayClickGUI;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import org.lwjgl.input.Keyboard;

@Parallel
@ModuleInfo(name = "ClickGUI", category = Category.CLIENT, keyCode = Keyboard.KEY_O, description = "ClickGUI of Stay")
public class ClickGUI extends Module {
    public Setting<Boolean> GuiMainMenu = setting("GuiMainMenu", true);
    public Setting<Boolean> drawChat = setting("drawChat", false);
    public Setting<Boolean> HotbarItem = setting("HotbarItem", false);
    public static ClickGUI instance;

    public ClickGUI() {
        instance = this;
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            if (!(mc.currentScreen instanceof StayClickGUI)) {
                mc.displayGuiScreen(new StayClickGUI());
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.currentScreen != null && mc.currentScreen instanceof StayClickGUI) {
            mc.displayGuiScreen(null);
        }
        ConfigManager.saveAll();
    }

}
