package dev.cuican.staypro.module.modules.misc;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;

import dev.cuican.staypro.setting.Setting;
import net.minecraft.client.gui.GuiGameOver;
@ModuleInfo(name = "AntiDeathScreen", category = Category.MISC, description = "AntiDeathScreen")

public class AntiDeathScreen extends Module {

    @Override
    public void onTick() {
        if (mc.currentScreen instanceof GuiGameOver) {
                mc.player.respawnPlayer();
                mc.displayGuiScreen(null);
        }
    }

}
