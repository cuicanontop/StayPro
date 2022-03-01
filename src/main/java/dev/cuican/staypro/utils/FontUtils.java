package dev.cuican.staypro.utils;


import dev.cuican.staypro.utils.graphics.font.CFont;
import dev.cuican.staypro.utils.graphics.font.CFontRenderer;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class FontUtils {
    public static CFontRenderer Comfortaa = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/fonts/Comfortaa-Bold.ttf", 18.0f, 0), true, false);
    public static CFontRenderer Icon = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/fonts/Icon.ttf", 18.0f, 0), true, false);
     private static final Minecraft mc = Minecraft.getMinecraft();
    public static float drawStringWithShadow(String text, int x, int y, int color){
        return mc.fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    public static int getStringWidth(String str){
        return mc.fontRenderer.getStringWidth(str);
    }

    public static int getFontHeight(){
        return mc.fontRenderer.FONT_HEIGHT;
    }

    public static float drawKeyStringWithShadow(String text, int x, int y, int color) {
        return mc.fontRenderer.drawStringWithShadow(text, x, y, color);
    }
}

