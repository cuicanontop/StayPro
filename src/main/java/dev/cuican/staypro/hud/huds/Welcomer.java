package dev.cuican.staypro.hud.huds;

import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.engine.AsyncRenderer;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.hud.HUDModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

@ModuleInfo(name = "Welcomer", category = Category.HUD)
public class Welcomer extends HUDModule {

    public Welcomer() {

        asyncRenderer = new AsyncRenderer() {
            @Override
            public void onUpdate(ScaledResolution resolution, int mouseX, int mouseY) {
                String text = "Welcome " + Minecraft.getMinecraft().player.getName() + "!Have a nice day :)";
                drawAsyncString(text, x, y, GUIManager.getColor3I());
                width = FontManager.getWidth(text);
                height = FontManager.getHeight();
            }
        };
    }

    @Override
    public void onHUDRender(ScaledResolution resolution) {
        asyncRenderer.onRender();
    }

}
