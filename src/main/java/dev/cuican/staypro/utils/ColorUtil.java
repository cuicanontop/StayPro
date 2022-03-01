package dev.cuican.staypro.utils;

import dev.cuican.staypro.module.modules.render.Wireframe;

import java.awt.*;

public class ColorUtil {
    public static int toARGB(int r, int g, int b, int a) {
        return (new Color(r, g, b, a)).getRGB();
    }
    public static Color getColor(int hex) {
        return new Color(hex);
    }
    public static int changeAlpha(int origColor, int userInputedAlpha) {
        origColor &= 0xFFFFFF;
        return userInputedAlpha << 24 | origColor;
    }
    public static Integer calculateAlphaChangeColor(int oldAlpha, int newAlpha, int step, int currentStep) {
        return Math.max(0, Math.min(255, oldAlpha + (newAlpha - oldAlpha) * Math.max(0, Math.min(step, currentStep)) / step));
    }
    public static Color shadeColour(Color color, int precent) {
        int r = color.getRed() * (100 + precent) / 100;
        int g = color.getGreen() * (100 + precent) / 100;
        int b = color.getBlue() * (100 + precent) / 100;
        return new Color(r, g, b);
    }

    public static int shadeColour(int color, int precent) {
        int r = ((color & 16711680) >> 16) * (100 + precent) / 100;
        int g = ((color & '\uff00') >> 8) * (100 + precent) / 100;
        int b = (color & 255) * (100 + precent) / 100;
        return (new Color(r, g, b)).hashCode();
    }

    public static int getAlpha(int hex) {
        return hex >> 24 & 255;
    }
    public static Color rainbow(final int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360.0;
        return Color.getHSBColor((float)(rainbowState / 360.0), Wireframe.getInstance().rainbowSaturation.getValue() / 255.0f, Wireframe.getInstance().rainbowBrightness.getValue() / 255.0f);
    }

    public static int getRed(int hex) {
        return hex >> 16 & 255;
    }

    public static int getGreen(int hex) {
        return hex >> 8 & 255;
    }

    public static int getBlue(int hex) {
        return hex & 255;
    }

    public static int getHoovered(int color, boolean isHoovered) {
        return isHoovered ? (color & 0x7F7F7F) << 1 : color;
    }




    public static int toRGBA(final int r, final int g, final int b) {
        return toRGBA(r, g, b, 255);
    }

    public static int toRGBA(final int r, final int g, final int b, final int a) {
        return (r << 16) + (g << 8) + b + (a << 24);
    }

    public static int toRGBA(final float r, final float g, final float b, final float a) {
        return toRGBA((int)(r * 255.0f), (int)(g * 255.0f), (int)(b * 255.0f), (int)(a * 255.0f));
    }


    public static int toRGBA(final float[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        }
        return toRGBA(colors[0], colors[1], colors[2], colors[3]);
    }

    public static int toRGBA(final double[] colors) {
        if (colors.length != 4) {
            throw new IllegalArgumentException("colors[] must have a length of 4!");
        }
        return toRGBA((float)colors[0], (float)colors[1], (float)colors[2], (float)colors[3]);
    }

    public static int toRGBA(final Color color) {
        return toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
}
