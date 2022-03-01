package dev.cuican.staypro.gui.components;

import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.gui.Component;
import dev.cuican.staypro.gui.Panel;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.NumberSetting;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.setting.settings.*;
import dev.cuican.staypro.utils.SoundUtil;
import dev.cuican.staypro.utils.Timer;
import dev.cuican.staypro.utils.graphics.AnimationUtil;
import dev.cuican.staypro.utils.graphics.RenderUtils2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleButton extends Component {

    public List<Component> settings = new ArrayList<>();
    public Module module;
    public Timer buttonTimer = new Timer();

    public ModuleButton(Module module, int width, int height, Panel father) {
        this.module = module;
        this.width = width;
        this.height = height;
        this.father = father;
        setup();
    }

    public void setup() {
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BooleanSetting)
                settings.add(new BooleanButton((BooleanSetting) setting, width, height, father));
            else if (setting instanceof IntSetting || setting instanceof FloatSetting || setting instanceof DoubleSetting)
                settings.add(new NumberSlider((NumberSetting<?>) setting, width, height, father));
            else if (setting instanceof ModeSetting)
                settings.add(new ModeButton((ModeSetting) setting, width, height, father));
            else if (setting instanceof BindSetting)
                settings.add(new BindButton((BindSetting) setting, width, height, father));
            else if (setting instanceof ActionSetting)
                settings.add(new ActionButton((ActionSetting) setting, width, height, father));

        }
    }
    AnimationUtil animationUtils = new AnimationUtil();
    private float ys = 0.0f;

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        RenderUtils2D.drawRect(x, y - 1, x + width, y + height+1, 0x85000000);

        if (module.isEnabled()) {
            ys = (float) animationUtils.animate( width,ys,0.2);
        }else {
            ys = (float) animationUtils.animate( 0.0f,ys,0.2);

        }


        if(ys<=0.0f)ys=0.0f;

        RenderUtils2D.drawRect(x-1.5f, y, x + ys, y + height, GUIManager.getColor4I());
        font.drawString(module.name, x + 3, (int) (y + height / 2 - font.getHeight() / 2f) + 2, fontColor);
        FontManager.drawIcon(x + width - 2 - FontManager.getIconWidth(), y + 5, new Color(230, 230, 230, 230));
    }


    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!isHovered(mouseX, mouseY))
            return false;
        if (mouseButton == 0) {
            module.toggle();
            SoundUtil.playButtonClick();
        } else if (mouseButton == 1) {
            buttonTimer.reset();
            isExtended = !isExtended;
            SoundUtil.playButtonClick();
        }
        return true;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        for (Component setting : settings) {
            setting.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (Component setting : settings) {
            setting.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public String getDescription() {
        return module.description;
    }

}
