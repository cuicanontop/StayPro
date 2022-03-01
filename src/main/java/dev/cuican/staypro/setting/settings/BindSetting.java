package dev.cuican.staypro.setting.settings;

import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.KeyBind;

public class BindSetting extends Setting<KeyBind> {
    public BindSetting(String name, KeyBind defaultValue) {
        super(name, defaultValue);
    }
}
