package dev.cuican.staypro.gui.components;

import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.setting.settings.ActionSetting;
import dev.cuican.staypro.utils.graphics.RenderUtils2D;
import dev.cuican.staypro.gui.Component;
import dev.cuican.staypro.gui.Panel;

public class ActionButton extends Component {

    ActionSetting setting;

    public ActionButton(ActionSetting setting, int width, int height, Panel father) {
        this.setting = setting;
        this.width = width;
        this.height = height;
        this.father = father;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        RenderUtils2D.drawRect(x, y, x + width, y + height, 0x85000000);
        font.drawString(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 2, getHoveredColor(mouseX, mouseY, GUIManager.getColor3I()));
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!isHovered(mouseX, mouseY))
            return false;

        if (mouseButton == 0) {
            setting.getValue().invoke();
        }
        return true;

    }

    @Override
    public String getDescription() {
        return setting.getDescription();
    }

}
