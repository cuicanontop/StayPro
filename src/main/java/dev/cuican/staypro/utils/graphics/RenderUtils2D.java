package dev.cuican.staypro.utils.graphics;

import dev.cuican.staypro.module.modules.render.Nametags;
import dev.cuican.staypro.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

/**
 * Author B_312
 * last update on Sep 12th 2021
 */
public class RenderUtils2D {
    public static float drawStringWithShadow(String p_Name, float p_X, float p_Y, int p_Color) {
        return (float) Wrapper.getMinecraft().fontRenderer.drawStringWithShadow(p_Name, p_X, p_Y, p_Color);

    }
    public static final void resetColour() {
        glColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static final void glColor(float red, float green, float blue, float alpha) {
        GL11.glColor4f(red, green, blue, alpha);
    }
    public static final void blend(boolean state) {
        if (state) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            return;
        }
        GlStateManager.disableBlend();
    }

    public static final void matrix(boolean state) {
        if (state) {
            GL11.glPushMatrix();
            return;
        }
        GL11.glPopMatrix();
    }
    private static Minecraft mc = Minecraft.getMinecraft();
    public static void drawNametag(double x, double y, double z, String[] text, GSColor color, int type,float healthP) {
        double dist = mc.player.getDistance(x, y, z);
        double scale = 1, offset = 0;
        int start = 0;
        switch (type) {
            case 0:
                scale = dist / 20 * Math.pow(1.2589254, 0.1 / (dist < 25 ? 0.5 : 2));
                scale = Math.min(Math.max(scale, .5), 5);
                offset = scale > 2 ? scale / 2 : scale;
                scale /= 40;
                start = 10;
                break;
            case 1:
                scale = -((int) dist) / 6.0;
                if (scale < 1) scale = 1;
                scale *= 2.0 / 75.0;
                break;
            case 2:
                scale = 0.0018 + 0.003 * dist;
                if (dist <= 8.0) scale = 0.0245;
                start = -8;
                break;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x - mc.getRenderManager().viewerPosX, y + offset - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1 : 1, 0, 0);
        GlStateManager.scale(-scale, -scale, scale);
        if (type == 2) {
            double width = 0;
            GSColor bcolor = new GSColor(0, 0, 0, 51);

            if (Nametags.INSTANCE.customColor.getValue()) {
                bcolor = new GSColor(Nametags.INSTANCE.red.getValue(), Nametags.INSTANCE.green.getValue(), Nametags.INSTANCE.blue.getValue());
            }
            for (String s : text) {
                double w = FontUtil.getStringWidth(s) / 2f;
                if (w > width) {
                    width = w;
                }
            }


            float allWidth = (float) (-width);

            RenderUtil.drawRect(-allWidth+2 , 2, (allWidth)-1, 1, Colors.getColor(100,0,0, 255));
            RenderUtil.drawRect(-allWidth+2, 2, (allWidth - (allWidth * (1 - healthP)) * 2)-1, 1, Colors.RED.getRGB());


            drawBorderedRect(-width - 1, -mc.fontRenderer.FONT_HEIGHT, width + 2, 1, 1.8f, new GSColor(0, 4, 0, 85), bcolor);
        }
        GlStateManager.enableTexture2D();
        for (int i = 0; i < text.length; i++) {
            FontUtils.drawStringWithShadow(text[i], -FontUtil.getStringWidth(text[i]) / 2, i * (mc.fontRenderer.FONT_HEIGHT + 5) + start, color.getRGB());
        }
        GlStateManager.disableTexture2D();
        if (type != 2) {
            GlStateManager.popMatrix();
        }
    }
    private static void drawBorderedRect(double x, double y, double x1, double y1, float lineWidth, GSColor inside, GSColor border) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        inside.glColor();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(x, y1, 0).endVertex();
        bufferbuilder.pos(x1, y1, 0).endVertex();
        bufferbuilder.pos(x1, y, 0).endVertex();
        bufferbuilder.pos(x, y, 0).endVertex();
        tessellator.draw();
        border.glColor();
        GlStateManager.glLineWidth(lineWidth);
        bufferbuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(x, y, 0).endVertex();
        bufferbuilder.pos(x, y1, 0).endVertex();
        bufferbuilder.pos(x1, y1, 0).endVertex();
        bufferbuilder.pos(x1, y, 0).endVertex();
        bufferbuilder.pos(x, y, 0).endVertex();
        tessellator.draw();
    }

    private static final Tessellator tessellator = Tessellator.getInstance();
    private static final BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
    public static void drawRectBase(int left, double top, int right, double bottom, int color) {
        double side;
        if (left < right) {
            side = (double)left;
            left = right;
            right = (int)side;
        }

        if (top < bottom) {
            side = top;
            top = bottom;
            bottom = side;
        }

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color((float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, (float)(color >> 24 & 255) / 255.0F);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double)left, bottom, 0.0D).endVertex();
        bufferbuilder.pos((double)right, bottom, 0.0D).endVertex();
        bufferbuilder.pos((double)right, top, 0.0D).endVertex();
        bufferbuilder.pos((double)left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawHLineG(int x, int y, int length, int color, int color2) {
        drawSidewaysGradientRect((float)x, (float)y, (float)(x + length), (float)(y + 1), color, color2);
    }
    public static void drawSidewaysGradientRect(float left, float top, float right, float bottom, int startColor, int endColor) {
        float c = (float)(startColor >> 24 & 255) / 255.0F;
        float c1 = (float)(startColor >> 16 & 255) / 255.0F;
        float c2 = (float)(startColor >> 8 & 255) / 255.0F;
        float c3 = (float)(startColor & 255) / 255.0F;
        float c4 = (float)(endColor >> 24 & 255) / 255.0F;
        float c5 = (float)(endColor >> 16 & 255) / 255.0F;
        float c6 = (float)(endColor >> 8 & 255) / 255.0F;
        float c7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 0.0D).color(c1, c2, c3, c).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0.0D).color(c5, c6, c7, c4).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, 0.0D).color(c5, c6, c7, c4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0.0D).color(c1, c2, c3, c).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawBorderedRect(int left, double top, int right, double bottom, int borderWidth, int insideColor, int borderColor, boolean hover) {
        if (hover) {
            insideColor = ColorUtil.shadeColour(insideColor, -20);
            borderColor = ColorUtil.shadeColour(borderColor, -20);
        }

        drawRectBase(left + borderWidth, top + (double)borderWidth, right - borderWidth, bottom - (double)borderWidth, insideColor);
        drawRectBase(left, top + (double)borderWidth, left + borderWidth, bottom - (double)borderWidth, borderColor);
        drawRectBase(right - borderWidth, top + (double)borderWidth, right, bottom - (double)borderWidth, borderColor);
        drawRectBase(left, top, right, top + (double)borderWidth, borderColor);
        drawRectBase(left, bottom - (double)borderWidth, right, bottom, borderColor);
    }

    public static void prepareGl() {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(GL_SMOOTH);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GlStateManager.disableCull();
    }

    public static void releaseGl() {
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(GL_FLAT);
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.enableCull();
    }

    public static void drawRectOutline(float x, float y, float endX, float endY, int color) {
        drawCustomRectOutline(x, y, endX, endY, 1.0F, color, color, color, color);
    }

    public static void drawRectOutline(float x, float y, float endX, float endY, float lineWidth, int color) {
        drawCustomRectOutline(x, y, endX, endY, lineWidth, color, color, color, color);
    }

    public static void drawCustomRectOutline(float x, float y, float endX, float endY, int rightTop, int leftTop, int leftDown, int rightDown) {
        drawCustomRectOutline(x, y, endX, endY, 1.0F, rightTop, leftTop, leftDown, rightDown);
    }

    public static void drawCustomRectOutline(float x, float y, float endX, float endY, float lineWidth, int rightTop, int leftTop, int leftDown, int rightDown) {
        drawCustomLine(endX, y, x, y, lineWidth, rightTop, leftTop); //RightTop -> LeftTop
        drawCustomLine(x, y, x, endY, lineWidth, leftTop, leftDown); //LeftTop -> LeftDown
        drawCustomLine(x, endY, endX, endY, lineWidth, leftDown, rightDown); //LeftDown -> RightDown
        drawCustomLine(endX, endY, endX, y, lineWidth, rightDown, rightTop); //RightDown -> RightTop
    }

    public static void drawRect(float x, float y, float endX, float endY, int color) {
        drawCustomRect(x, y, endX, endY, color, color, color, color);
    }

    public static void drawCustomRect(float x, float y, float endX, float endY, int rightTop, int leftTop, int leftDown, int rightDown) {
        prepareGl();

        VertexBuffer.begin(GL_QUADS);
        VertexBuffer.put(endX, y, rightTop);
        VertexBuffer.put(x, y, leftTop);
        VertexBuffer.put(x, endY, leftDown);
        VertexBuffer.put(endX, endY, rightDown);
        VertexBuffer.end();

        releaseGl();
    }

    public static void drawLine(float startX, float startY, float endX, float endY, int color) {
        drawCustomLine(startX, startY, endX, endY, 1.0F, color, color);
    }

    public static void drawLine(float startX, float startY, float endX, float endY, float lineWidth, int color) {
        drawCustomLine(startX, startY, endX, endY, lineWidth, color, color);
    }

    public static void drawCustomLine(float startX, float startY, float endX, float endY, int startColor, int endColor) {
        drawCustomLine(startX, startY, endX, endY, 1.0F, startColor, endColor);
    }

    public static void drawCustomLine(float startX, float startY, float endX, float endY, float lineWidth, int startColor, int endColor) {
        prepareGl();

        glLineWidth(lineWidth);

        VertexBuffer.begin(GL_LINES);
        VertexBuffer.put(startX, startY, startColor);
        VertexBuffer.put(endX, endY, endColor);
        VertexBuffer.end();

        glLineWidth(1F);

        releaseGl();
    }

}
