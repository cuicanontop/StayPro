package dev.cuican.staypro.utils;

import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class Colors extends Color {

    public Colors(int r, int g, int b) {
        super(r, g, b);
    }

    public Colors(int r, int g, int b, int a) {
        super(r,g,b,a);
    }

    public void glColor() {
        GlStateManager.color(getRed()/255.0f,getGreen()/255.0f,getBlue()/255.0f,getAlpha()/255.0f);
    }
    public static int getColor(final Color color) {
        return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getColor(final int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }

    public static int getColor(final int brightness, final int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    public static int getColor(final int red, final int green, final int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(final int red, final int green, final int blue, final int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }
}
