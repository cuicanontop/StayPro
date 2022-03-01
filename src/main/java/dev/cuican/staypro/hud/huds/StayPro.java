package dev.cuican.staypro.hud.huds;

import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.graphics.font.CFontRenderer;
import dev.cuican.staypro.utils.math.InfoCalculator;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

@ModuleInfo(name = "StayPro", category = Category.HUD)
public class StayPro extends HUDModule {


    @Override
    public void onHUDRender(ScaledResolution resolution) {
        String Final = "STAYPRO";

        FontManager.haofont.drawString(Final, x + 2, y + 4, GUIManager.getColor3I(),true);

        width = FontManager.haofont.getStringWidth(Final) + 4;
        height = FontManager.haofont.getHeight();
    }
}
