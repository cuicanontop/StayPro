package dev.cuican.staypro.hud.huds;

import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.engine.AsyncRenderer;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.utils.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

@ModuleInfo(name = "FPS",category = Category.HUD)
public class FPS extends HUDModule {

    public FPS() {
        asyncRenderer = new AsyncRenderer() {
            @Override
            public void onUpdate(ScaledResolution resolution, int mouseX, int mouseY) {
                String text = "FPS " + ChatUtil.SECTIONSIGN + "f"  + Minecraft.getDebugFPS();
                drawAsyncString(text, x+ 2, y+ 4, GUIManager.getColor3I());
                width =mc.fontRenderer.getStringWidth(text) + 4;
                height = FontManager.getHeight();

            }
        };
    }

    @Override
    public void onHUDRender(ScaledResolution resolution) {
        asyncRenderer.onRender();
    }
}
