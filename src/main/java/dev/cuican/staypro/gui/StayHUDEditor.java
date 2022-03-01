package dev.cuican.staypro.gui;

import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.module.modules.client.HUDEditor;
import dev.cuican.staypro.utils.graphics.RenderUtils2D;
import dev.cuican.staypro.utils.particles.ParticleSystem;
import dev.cuican.staypro.gui.renderers.HUDEditorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import static dev.cuican.staypro.gui.StayClickGUI.description;
import static dev.cuican.staypro.gui.StayClickGUI.white;

@SuppressWarnings("ALL")
public class StayHUDEditor extends GuiScreen {

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
        if (ModuleManager.getModule(HUDEditor.class).isEnabled()) {
            ModuleManager.getModule(HUDEditor.class).disable();
        }
    }


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
        HUDEditorRenderer.instance.drawScreen(mouseX, mouseY, partialTicks);
        if (description != null) {
            RenderUtils2D.drawRect(description.b.x + 10, description.b.y, description.b.x + 12 + FontManager.getWidth(description.a), description.b.y + FontManager.getHeight() + 4, 0x85000000);
            RenderUtils2D.drawRectOutline(description.b.x + 10, description.b.y, description.b.x + 12 + FontManager.getWidth(description.a), description.b.y + FontManager.getHeight() + 4, GUIManager.getColor4I());
            FontManager.draw(description.a, description.b.x + 11, description.b.y + 4, white);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        HUDEditorRenderer.instance.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        HUDEditorRenderer.instance.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        HUDEditorRenderer.instance.mouseReleased(mouseX, mouseY, state);
    }
}
