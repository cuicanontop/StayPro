package dev.cuican.staypro.notification;


import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.utils.RenderUtil;
import dev.cuican.staypro.utils.Wrapper;
import dev.cuican.staypro.utils.graphics.AnimationUtil;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class Notification {
    public String text;
    public double width, height = 30;
    public float x;
    Type type;
    public float y, position;
    public boolean in = true;
    AnimationUtil animationUtils = new AnimationUtil();
    AnimationUtil yAnimationUtils = new AnimationUtil();


    public Notification(String text, Type type) {
        this.text = text;
        this.type = type;
        width =  FontManager.fontRenderer.getStringWidth(text) + 25;
        x = (float) width;
    }

    public void onRender() {
        int i = 0;
        for (Notification notification : NotificationManager.notifications) {
            if (notification == this) {
                break;
            }
            i++;
        }

        y = yAnimationUtils.animate((float) ((float) i * (height + 5)), y, 0.1f);
        ScaledResolution sr = new ScaledResolution(Wrapper.getMinecraft());
        RenderUtil.drawRectS(sr.getScaledWidth() + x - width,
                sr.getScaledHeight() - 50 - y - height,
                sr.getScaledWidth() + x,
                sr.getScaledHeight() - 50 - y,
                new Color(0, 0, 0, 165).getRGB());
       /* RenderUtil.drawShadow((float) (sr.getScaledWidth() + x - width), (float) (sr.getScaledHeight() - 50 - y - height), sr.getScaledWidth() + x, sr.getScaledHeight() - 50 - y, 5);
*/        FontManager.fontRenderer.drawStringWithShadow(text, ((float) (sr.getScaledWidth() + x - width + 10)), ((float) (sr.getScaledHeight() - 50f - y - 18)), new Color(204, 204, 204, 232).getRGB());
    }

    public enum Type {
        Success,
        Error,
        Info
    }
}