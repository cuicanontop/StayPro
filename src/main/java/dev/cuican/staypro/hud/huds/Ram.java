package dev.cuican.staypro.hud.huds;


import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.engine.AsyncRenderer;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.math.InfoCalculator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

@ModuleInfo(name = "Ram",category = Category.HUD)
public class Ram extends HUDModule {

    public Ram() {

        asyncRenderer = new AsyncRenderer() {
            @Override
            public void onUpdate(ScaledResolution resolution, int mouseX, int mouseY) {

                String text = "Ram Usage " + ChatUtil.SECTIONSIGN + "f" + InfoCalculator.memory();
                drawAsyncString(text, x+ 2, y+ 4, GUIManager.getColor3I());
                width = FontManager.getWidth(text)+2;
                height = FontManager.getHeight()+4;
            }
        };
    }

    @Override
    public void onHUDRender(ScaledResolution resolution) {
        asyncRenderer.onRender();
    }

}
