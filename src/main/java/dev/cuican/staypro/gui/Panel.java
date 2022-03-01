package dev.cuican.staypro.gui;

import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.gui.components.ModuleButton;
import dev.cuican.staypro.gui.renderers.ClickGUIRenderer;
import dev.cuican.staypro.gui.renderers.HUDEditorRenderer;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.utils.Timer;
import dev.cuican.staypro.utils.graphics.RenderUtils2D;
import dev.cuican.staypro.utils.graphics.font.CFontRenderer;
import dev.cuican.staypro.utils.math.Pair;
import dev.cuican.staypro.utils.math.Vec2I;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Panel {

    public int x, y, width, height;
    public Category category;

    public boolean extended;
    boolean dragging;

    int x2, y2;

    CFontRenderer font;

    public List<ModuleButton> elements = new ArrayList<>();

    public Panel(Category category, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.extended = true;
        this.dragging = false;
        this.category = category;
        font = FontManager.fontRenderer;
        setup();
    }

    Timer panelTimer = new Timer();

    public void setup() {
        for (Module m : ModuleManager.getModules()) {
            if (m.category == category) {
                elements.add(new ModuleButton(m, width - 10, height - 2, this));
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.dragging) {
            x = x2 + mouseX;
            y = y2 + mouseY;
        }


        int panelColor = 0x85000000;
        RenderUtils2D.drawRect(x, y, x + width, y + height, GUIManager.getColor4I());
        font.drawString(category.categoryName, x + (width / 2f - font.getStringWidth(category.categoryName) / 2f), y + height / 2f - font.getHeight() / 2f + 2, 0xffefefef);
        FontManager.fontbaet.drawString(category.isIoce,x + (width -  FontManager.fontbaet.getStringWidth(category.isIoce)-15 ),y+height/2f-FontManager.fontbaet.getHeight()/2f+1,0xffefefef);
        RenderUtils2D.drawRect(x, y + height, x + width, y + height , panelColor);

        if (!elements.isEmpty()) {
            int startY = y + height + 3;
            int index = 0;
            for (ModuleButton button : elements) {
                index++;
                if (extended) {
                    if (!panelTimer.passed(index * 25)) continue;
                } else {
                    if (panelTimer.passed((elements.size() - index) * 25)) continue;
                }
                button.solvePos(true);
                button.y = startY;
                button.render(mouseX, mouseY, partialTicks);
                startY += height - 1;

                if (button.isHovered(mouseX, mouseY) && !button.getDescription().equals(""))
                    StayClickGUI.description = new Pair<>(button.getDescription(), new Vec2I(mouseX, mouseY));

                int settingIndex = -1;
                List<Component> visibleSettings = button.settings.stream().filter(Component::isVisible).collect(Collectors.toList());

                for (Component component : visibleSettings) {
                    settingIndex++;
                    if (button.isExtended) {
                        if (!button.buttonTimer.passed(settingIndex * 25)) continue;
                    } else {
                        if (button.buttonTimer.passed((visibleSettings.size() - settingIndex) * 25)) continue;
                    }
                    component.solvePos(true);
                    component.y = startY;
                    component.render(mouseX, mouseY, partialTicks);
                    if (component.isHovered(mouseX, mouseY) && !component.getDescription().equals(""))
                        StayClickGUI.description = new Pair<>(component.getDescription(), new Vec2I(mouseX, mouseY));
                    startY += height - 2;
                }

                startY += 1;

            }
            if (StayClickGUI.description != null) {
                RenderUtils2D.drawRect(StayClickGUI.description.b.x + 10, StayClickGUI.description.b.y, StayClickGUI.description.b.x + 12 + FontManager.getWidth(StayClickGUI.description.a), StayClickGUI.description.b.y + FontManager.getHeight() + 4, 0x85000000);
                RenderUtils2D.drawRectOutline(StayClickGUI.description.b.x + 10, StayClickGUI.description.b.y, StayClickGUI.description.b.x + 12 + FontManager.getWidth(StayClickGUI.description.a), StayClickGUI.description.b.y + FontManager.getHeight() + 4, GUIManager.getColor4I());
                FontManager.draw(StayClickGUI.description.a, StayClickGUI.description.b.x + 11, StayClickGUI.description.b.y + 4, StayClickGUI.white);
            }
            RenderUtils2D.drawLine(x+0.5f , y + height, x , startY+2f, GUIManager.getColor4I());
            RenderUtils2D.drawLine(x + width-0.5f, y + height, x + width-0.5f, startY+2f, GUIManager.getColor4I());
            RenderUtils2D.drawLine(x +0.5f, startY+2f, x + width-0.5f , startY+2f, GUIManager.getColor4I());
        }

    }


    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
            x2 = this.x - mouseX;
            y2 = this.y - mouseY;
            dragging = true;
            if (category.isHUD)
                Collections.swap(HUDEditorRenderer.instance.panels, 0, HUDEditorRenderer.instance.panels.indexOf(this));
            else Collections.swap(ClickGUIRenderer.instance.panels, 0, ClickGUIRenderer.instance.panels.indexOf(this));
            return true;
        }
        if (mouseButton == 1 && isHovered(mouseX, mouseY)) {
            extended = !extended;
            panelTimer.reset();
            return true;
        }
        return false;
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.dragging = false;
        }
        for (Component part : elements) {
            part.mouseReleased(mouseX, mouseY, state);
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        for (Component part : elements) {
            part.keyTyped(typedChar, keyCode);
        }
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= Math.min(x, x + width) && mouseX <= Math.max(x, x + width) && mouseY >= Math.min(y, y + height) && mouseY <= Math.max(y, y + height);
    }

}
