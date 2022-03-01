package dev.cuican.staypro.utils;

import java.awt.Color;

public class Rainbow {
    public static int getRainbow(float speed, float saturation, float brightness) {
        float hue = (float)(System.currentTimeMillis() % 11520L) / 11520.0f * speed;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }
    public static Color getColour() {
        return fromHSB((float)(System.currentTimeMillis() % 11520L) / 11520.0F, 1.0F, 1.0F);
    }

    public static Color getFurtherColour(int offset) {
        return fromHSB((float)((System.currentTimeMillis() + (long)offset) % 11520L) / 11520.0F, 1.0F, 1.0F);
    }
    public static Color fromHSB(float hue, float saturation, float brightness) {
        return Color.getHSBColor(hue, saturation, brightness);
    }
    public static Color getRainbowColor(float speed, float saturation, float brightness) {
        return new Color(Rainbow.getRainbow(speed, saturation, brightness));
    }

    public static Color getRainbowColor(float speed, float saturation, float brightness, long add) {
        return new Color(Rainbow.getRainbow(speed, saturation, brightness, add));
    }

    public static int getRainbow(float speed, float saturation, float brightness, long add) {
        float hue = (float)((System.currentTimeMillis() + add) % 11520L) / 11520.0f * speed;
        return Color.HSBtoRGB(hue, saturation, brightness);
    }
}

