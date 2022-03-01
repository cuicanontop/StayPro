package dev.cuican.staypro.gui;

import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.module.modules.client.ClickGUI;
import dev.cuican.staypro.utils.FadeUtils;
import dev.cuican.staypro.utils.Timer;
import dev.cuican.staypro.utils.graphics.RenderUtils2D;
import dev.cuican.staypro.utils.math.Pair;
import dev.cuican.staypro.utils.math.Vec2I;
import dev.cuican.staypro.utils.particles.ParticleSystem;
import dev.cuican.staypro.gui.renderers.ClickGUIRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@SuppressWarnings("ALL")
public class StayClickGUI extends GuiScreen {

    public static Pair<String, Vec2I> description = null;
    public static int white = new Color(255, 255, 255, 255).getRGB();
    FadeUtils fade = new FadeUtils(350);
    private final ParticleSystem particleSystem = new ParticleSystem(100);

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        if (GUIManager.getBackground().equals(GUIManager.Background.Blur) || GUIManager.getBackground().equals(GUIManager.Background.Both)) {
            if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null)
                Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
            Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }

    }


    @Override
    public void onGuiClosed() {
        if (Minecraft.getMinecraft().entityRenderer.getShaderGroup() != null)
            Minecraft.getMinecraft().entityRenderer.getShaderGroup().deleteShaderGroup();
        if (ModuleManager.getModule(ClickGUI.class).isEnabled()) {
            ModuleManager.getModule(ClickGUI.class).disable();
            fade.reset();
        }
    }

    public void mouseDrag() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            ClickGUIRenderer.instance.panels.forEach(component -> component.y -= 10);
        } else if (dWheel > 0) {
            ClickGUIRenderer.instance.panels.forEach(component -> component.y += 10);
        }
    }
    Timer panelTimer = new Timer();
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (GUIManager.getBackground().equals(GUIManager.Background.Shadow) || GUIManager.getBackground().equals(GUIManager.Background.Both)) {
            drawDefaultBackground();
        }
        description = null;
        if (GUIManager.isParticle()) {
            particleSystem.tick(10);
            particleSystem.render();
        }

        GL11.glPushMatrix();
        double scala = fade.getEpsEzFadeInGUI();
        GL11.glTranslated(Math.cos(width * scala), Math.cos(height ), Math.cos(width ));
        GL11.glScaled(scala, scala, scala);
        ClickGUIRenderer.instance.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glPopMatrix();
        if (description != null) {
            RenderUtils2D.drawRect(description.b.x + 10, description.b.y, description.b.x + 12 + FontManager.getWidth(description.a), description.b.y + FontManager.getHeight() + 4, 0x85000000);
            RenderUtils2D.drawRectOutline(description.b.x + 10, description.b.y, description.b.x + 12 + FontManager.getWidth(description.a), description.b.y + FontManager.getHeight() + 4, GUIManager.getColor4I());
            FontManager.draw(description.a, description.b.x + 11, description.b.y + 4, white);
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        ClickGUIRenderer.instance.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        ClickGUIRenderer.instance.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        ClickGUIRenderer.instance.mouseReleased(mouseX, mouseY, state);
    }
}
