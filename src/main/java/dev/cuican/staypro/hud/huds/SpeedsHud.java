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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

/**
 * Created by B_312 on 01/03/21
 */
@ModuleInfo(name = "Speeds", category = Category.HUD)
public class SpeedsHud extends HUDModule {
    FontRenderer fontRenderer = mc.fontRenderer;


    @Override
    public void onHUDRender(ScaledResolution resolution) {
        String Final = "Speed " + ChatUtil.SECTIONSIGN + "f" + InfoCalculator.speed(true,mc) + " km/h";
        FontManager.fontRenderer.drawString(Final, x + 2, y + 4, GUIManager.getColor3I());
        width = fontRenderer.getStringWidth(Final) + 4;
        height = FontManager.getHeight();
    }

}
