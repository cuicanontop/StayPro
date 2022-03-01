package dev.cuican.staypro.setting.settings;

import dev.cuican.staypro.setting.NumberSetting;

public class FloatSetting extends NumberSetting<Float> {
    public FloatSetting(String name, float defaultValue, float min, float max) {
        super(name, defaultValue, min, max);
    }
}
