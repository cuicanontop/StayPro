package dev.cuican.staypro.gui.window.components;


import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.concurrent.utils.Timer;
import dev.cuican.staypro.gui.Window;
import dev.cuican.staypro.gui.alt.utils.AbstractComponent;
import dev.cuican.staypro.utils.MathUtil;
import dev.cuican.staypro.utils.graphics.RenderUtils;

import java.awt.*;

public class ButtonAlt extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 22;
    private String title;
    private int preferredWidth;
    private int preferredHeight;
    private boolean hovered;
    private ActionEventListener listener;
    private final Timer timer = new Timer();

    public ButtonAlt(String title, int preferredWidth, int preferredHeight) {
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        this.setWidth(preferredWidth);
        this.setHeight(preferredHeight);
        this.setTitle(title);
        this.timer.reset();
    }

    public void reset(){
        this.timer.reset();
    }

    public ButtonAlt(String title) {
        this(title, PREFERRED_WIDTH, PREFERRED_HEIGHT);
    }

    @Override
    public void render() {
        double offset = !this.timer.passed(700) ? MathUtil.calculateDoubleChange(60+this.getWidth() , 0, 700, (int)this.timer.getPassedTimeMs()) : 0.0;
        RenderUtils.drawRoundedRectangle((-offset) + this.x, this.y, this.getWidth(), this.getHeight(), 7.0, this.hovered ? Window.SECONDARY_FOREGROUND : Window.TERTIARY_FOREGROUND);
        RenderUtils.drawRoundedRectangleOutline((-offset) + this.x, this.y, this.getWidth(), this.getHeight(), 7.0, 1.0f, RenderUtils.GradientDirection.LeftToRight, new Color(GUIManager.getColor3I()), new Color(GUIManager.getColor3I()));
        FontManager.fontRenderer.drawString(this.title, (float) ((-offset) + this.x + (float)this.getWidth() / 2.0f - (float)FontManager.fontRenderer.getStringWidth(this.title) / 2.0f), (float)this.y + (float)this.getHeight() / 2.0f - (float)FontManager.fontRenderer.getHeight() / 2.0f, Window.FONT.getRGB());
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        this.updateHovered(x, y, offscreen);
        return false;
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        this.hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + this.getWidth() && y <= this.y + this.getHeight();
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        if (button == 0) {
            this.updateHovered(x, y, offscreen);
            if (this.hovered && this.listener != null) {
                this.listener.onActionEvent();
                return true;
            }
        }
        return false;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.setWidth(Math.max(FontManager.fontRenderer.getStringWidth(title), this.preferredWidth));
        this.setHeight(Math.max(FontManager.fontRenderer.getHeight() * 5 / 4, this.preferredHeight));
    }

    public ActionEventListener getOnClickListener() {
        return this.listener;
    }

    public void setOnClickListener(ActionEventListener listener) {
        this.listener = listener;
    }
}
