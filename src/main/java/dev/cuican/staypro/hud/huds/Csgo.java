package dev.cuican.staypro.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.cuican.staypro.Stay;
import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.MathUtil;
import dev.cuican.staypro.utils.Rainbow;
import dev.cuican.staypro.utils.RenderUtil;
import dev.cuican.staypro.utils.graphics.RenderUtils2D;
import dev.cuican.staypro.utils.math.LagCompensator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


@ModuleInfo(name = "Csgo", category = Category.HUD)
public class Csgo extends HUDModule {


    @Override
    public void onHUDRender(ScaledResolution resolution) {
        if (nullCheck()) return;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

            String ping = getPing(mc.player) + "MS";
            String server = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer".toUpperCase() : mc.getCurrentServerData().serverIP.toUpperCase();
            String time = formatter.format(date);
            String text = " StayPro | " + time + " | " + server + " | " + ping;
            float widths = (float) (FontManager.fontRenderer.getStringWidth(text) + 10);
            int heights = 20;
            drawRect(x, y, (float) x + widths + 2.0F, y + heights, (new Color(5, 5, 5, 255)).getRGB());
            drawBorderedRect((double) x + 0.5D, (double) y + 0.5D, (double) ((float) x + widths) + 1.5D, (double) (y + heights) - 0.5D, 0.5D, (new Color(40, 40, 40, 255)).getRGB(), (new Color(60, 60, 60, 255)).getRGB(), true);
            drawBorderedRect(x + 2, y + 2, (float) x + widths, y + heights - 2, 0.5D, (new Color(22, 22, 22, 255)).getRGB(), (new Color(60, 60, 60, 255)).getRGB(), true);
            drawRect((double) x + 2.5D, (double) y + 2.5D, (double) ((float) x + widths) - 0.5D, (double) y + 4.5D, (new Color(9, 9, 9, 255)).getRGB());
            drawGradientSideways(x + 3.0F, y + 3, x + 4.0F + widths / 3.0F, y + 4, (new Color(81, 149, 219, 255)).getRGB(), (new Color(180, 49, 218, 255)).getRGB());
            drawGradientSideways(x + 4.0F + widths / 3.0F, y + 3, x + 4.0F + widths / 3.0F * 2.0F, y + 4, (new Color(180, 49, 218, 255)).getRGB(), (new Color(236, 93, 128, 255)).getRGB());
            drawGradientSideways(x + 4.0F + widths / 3.0F * 2.0F, y + 3, x + widths / 3.0F * 3.0F-1F, y + 4, (new Color(236, 93, 128, 255)).getRGB(), (new Color(235, 255, 0, 255)).getRGB());
           FontManager.fontRenderer.drawStringWithShadow(text, (float) (4 + x), (float) (8 + y), -1);
        width =mc.fontRenderer.getStringWidth(text) + 50;
        height = FontManager.getHeight()+10;
    }
    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float) (color >> 24 & 0xFF) / 255.0f;
        float f = (float) (color >> 16 & 0xFF) / 255.0f;
        float f1 = (float) (color >> 8 & 0xFF) / 255.0f;
        float f2 = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        bufferBuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferBuilder.pos(left, bottom, 0.0).endVertex();
        bufferBuilder.pos(right, bottom, 0.0).endVertex();
        bufferBuilder.pos(right, top, 0.0).endVertex();
        bufferBuilder.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    public static void drawBorderedRect(double left, double top, double right, double bottom, double borderWidth, int insideColor, int borderColor, boolean borderIncludedInBounds) {
        RenderUtil.drawRect(left - (!borderIncludedInBounds ? borderWidth : 0.0), top - (!borderIncludedInBounds ? borderWidth : 0.0), right + (!borderIncludedInBounds ? borderWidth : 0.0), bottom + (!borderIncludedInBounds ? borderWidth : 0.0), borderColor);
        RenderUtil.drawRect(left + (borderIncludedInBounds ? borderWidth : 0.0), top + (borderIncludedInBounds ? borderWidth : 0.0), right - (borderIncludedInBounds ? borderWidth : 0.0), bottom - (borderIncludedInBounds ? borderWidth : 0.0), insideColor);
    }
    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (float) (col1 >> 24 & 0xFF) / 255.0f;
        float f1 = (float) (col1 >> 16 & 0xFF) / 255.0f;
        float f2 = (float) (col1 >> 8 & 0xFF) / 255.0f;
        float f3 = (float) (col1 & 0xFF) / 255.0f;
        float f4 = (float) (col2 >> 24 & 0xFF) / 255.0f;
        float f5 = (float) (col2 >> 16 & 0xFF) / 255.0f;
        float f6 = (float) (col2 >> 8 & 0xFF) / 255.0f;
        float f7 = (float) (col2 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        GL11.glColor4f(f5, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
    private int getPing(final EntityPlayer player) {
        int ping = 0;
        try {
            ping = (int) MathUtil.clamp((float) Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime(), 1, 300.0f);
        } catch (NullPointerException ignored) {
        }
        return ping;
    }
    }



