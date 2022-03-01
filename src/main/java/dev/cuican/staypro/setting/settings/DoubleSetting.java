package dev.cuican.staypro.setting.settings;

import dev.cuican.staypro.setting.NumberSetting;

public class DoubleSetting extends NumberSetting<Double> {
    public DoubleSetting(String name, double defaultValue, double min, double max) {
        super(name, defaultValue, min, max);
    }
}
