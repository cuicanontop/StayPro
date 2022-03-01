package dev.cuican.staypro.utils;

import java.awt.Color;

public class ColorValue {
    public int Field725;
    public boolean Field726 = false;
    public int Field727 = 0;

    public int Method769() {
        if (this.Field726) {
            float[] fArray = Color.RGBtoHSB(this.Field725 >> 16 & 0xFF, this.Field725 >> 8 & 0xFF, this.Field725 & 0xFF, null);
            double d = Math.ceil((double)(System.currentTimeMillis() + 300L + (long)this.Field727) / 20.0);
            int n = Color.getHSBColor((float)((d %= 360.0) / 360.0), fArray[1], fArray[2]).getRGB();
            return n >> 16 & 0xFF;
        }
        return this.Field725 >> 16 & 0xFF;
    }

    public int Method770() {
        if (this.Field726) {
            float[] fArray = Color.RGBtoHSB(this.Field725 >> 16 & 0xFF, this.Field725 >> 8 & 0xFF, this.Field725 & 0xFF, null);
            double d = Math.ceil((double)(System.currentTimeMillis() + 300L + (long)this.Field727) / 20.0);
            int n = Color.getHSBColor((float)((d %= 360.0) / 360.0), fArray[1], fArray[2]).getRGB();
            return n >> 8 & 0xFF;
        }
        return this.Field725 >> 8 & 0xFF;
    }

    public ColorValue(int n) {
        this.Field725 = n;
    }

    public void Method771(int n) {
        this.Field725 = n;
    }

    public void Method772() {
        this.Field726 = !this.Field726;
    }

    public int Method773(int n) {
        if (this.Field726) {
            float[] fArray = Color.RGBtoHSB(this.Field725 >> 16 & 0xFF, this.Field725 >> 8 & 0xFF, this.Field725 & 0xFF, null);
            double d = Math.ceil((double)(System.currentTimeMillis() + 300L + (long)n + (long)this.Field727) / 20.0);
            int n2 = Color.getHSBColor((float)((d %= 360.0) / 360.0), fArray[1], fArray[2]).getRGB();
            int n3 = this.Field725 >> 24 & 0xFF;
            int n4 = n2 >> 16 & 0xFF;
            int n5 = n2 >> 8 & 0xFF;
            int n6 = n2 & 0xFF;
            return (n3 & 0xFF) << 24 | (n4 & 0xFF) << 16 | (n5 & 0xFF) << 8 | n6 & 0xFF;
        }
        return this.Field725;
    }

    public ColorValue(int n, boolean bl) {
        this.Field725 = n;
        this.Field726 = bl;
    }

    public int Method774() {
        if (this.Field726) {
            float[] fArray = Color.RGBtoHSB(this.Field725 >> 16 & 0xFF, this.Field725 >> 8 & 0xFF, this.Field725 & 0xFF, null);
            double d = Math.ceil((double)(System.currentTimeMillis() + 300L + (long)this.Field727) / 20.0);
            int n = Color.getHSBColor((float)((d %= 360.0) / 360.0), fArray[1], fArray[2]).getRGB();
            int n2 = this.Field725 >> 24 & 0xFF;
            int n3 = n >> 16 & 0xFF;
            int n4 = n >> 8 & 0xFF;
            int n5 = n & 0xFF;
            return (n2 & 0xFF) << 24 | (n3 & 0xFF) << 16 | (n4 & 0xFF) << 8 | n5 & 0xFF;
        }
        return this.Field725;
    }

    public Color Method775() {
        int n = this.Method774();
        int n2 = n >> 24 & 0xFF;
        int n3 = n >> 16 & 0xFF;
        int n4 = n >> 8 & 0xFF;
        int n5 = n & 0xFF;
        return new Color(n3, n4, n5, n2);
    }

    public ColorValue(int n, boolean bl, int n2) {
        this.Field725 = n;
        this.Field726 = bl;
        this.Field727 = n2;
    }

    public int Method776() {
        return this.Field727;
    }

    public static int Method777(String string) throws NumberFormatException {
        Integer n = Integer.decode(string);
        return n;
    }

    public int Method778() {
        return this.Field725;
    }

    public int Method779() {
        if (this.Field726) {
            float[] fArray = Color.RGBtoHSB(this.Field725 >> 16 & 0xFF, this.Field725 >> 8 & 0xFF, this.Field725 & 0xFF, null);
            double d = Math.ceil((double)(System.currentTimeMillis() + 300L + (long)this.Field727) / 20.0);
            int n = Color.getHSBColor((float)((d %= 360.0) / 360.0), fArray[1], fArray[2]).getRGB();
            return n & 0xFF;
        }
        return this.Field725 & 0xFF;
    }

    public void Method780(boolean bl) {
        this.Field726 = bl;
    }

    public void Method781(int n) {
        this.Field727 = n;
    }

    public int Method782() {
        return this.Field725 >> 24 & 0xFF;
    }

    public boolean Method783() {
        return this.Field726;
    }

    public ColorValue Method784(int n) {
        int n2 = this.Method774() >> 16 & 0xFF;
        int n3 = this.Method774() >> 8 & 0xFF;
        int n4 = this.Method774() & 0xFF;
        return new ColorValue((n & 0xFF) << 24 | (n2 & 0xFF) << 16 | (n3 & 0xFF) << 8 | n4 & 0xFF);
    }
}