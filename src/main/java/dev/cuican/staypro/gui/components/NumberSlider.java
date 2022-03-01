package dev.cuican.staypro.gui.components;

import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.setting.NumberSetting;
import dev.cuican.staypro.setting.settings.DoubleSetting;
import dev.cuican.staypro.setting.settings.FloatSetting;
import dev.cuican.staypro.setting.settings.IntSetting;
import dev.cuican.staypro.utils.graphics.RenderUtils2D;
import dev.cuican.staypro.gui.Component;
import dev.cuican.staypro.gui.Panel;
import net.minecraft.util.math.MathHelper;

public class NumberSlider extends Component {

    boolean sliding = false;
    NumberSetting<?> setting;

    public NumberSlider(NumberSetting<?> setting, int width, int height, Panel father) {
        this.width = width;
        this.height = height;
        this.father = father;
        this.setting = setting;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (!setting.isVisible()) sliding = false;

        RenderUtils2D.drawRect(x, y, x + width, y + height, 0x85000000);
        String displayValue = setting instanceof IntSetting ? setting.getValue().toString() : String.format("%.1f", setting.getValue().doubleValue());
        double percentBar = (setting.getValue().doubleValue() - setting.getMin().doubleValue()) / (setting.getMax().doubleValue() - setting.getMin().doubleValue());
        double tempWidth = (width - 4) * percentBar;
        RenderUtils2D.drawRect(x + 3, y + 1, x + 3 + (int) tempWidth, y + height, GUIManager.getColor4I());

        if (this.sliding) {
            double diff = setting.getMax().doubleValue() - setting.getMin().doubleValue();
            double val = setting.getMin().doubleValue() + (MathHelper.clamp((mouseX - (double) (x + 3)) / (double) (width - 4), 0, 1)) * diff;
            if (setting instanceof DoubleSetting) {
                ((DoubleSetting) setting).setValue(val);
            } else if (setting instanceof FloatSetting) {
                ((FloatSetting) setting).setValue((float) val);
            } else if (setting instanceof IntSetting) {
                ((IntSetting) setting).setValue((int) val);
            }
        }

        font.drawString(setting.getName(), x + 5, (int) (y + height / 2 - font.getHeight() / 2f) + 2, fontColor);
        font.drawString(String.valueOf(displayValue), x + width - 3 - font.getStringWidth(String.valueOf(displayValue)), (int) (y + height / 2 - font.getHeight() / 2f) + 2, isHovered(mouseX, mouseY) ? fontColor : 0x909090);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!setting.isVisible() || !isHovered(mouseX, mouseY))
            return false;
        if (mouseButton == 0) {
            this.sliding = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        sliding = false;
    }

    @Override
    public boolean isVisible() {
        return setting.isVisible();
    }

    @Override
    public String getDescription() {
        return setting.getDescription();
    }

}
