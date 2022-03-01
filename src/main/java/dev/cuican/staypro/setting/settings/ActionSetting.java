package dev.cuican.staypro.setting.settings;

import dev.cuican.staypro.concurrent.task.VoidTask;
import dev.cuican.staypro.setting.Setting;

public class ActionSetting extends Setting<VoidTask> {
    public ActionSetting(String name, VoidTask defaultValue) {
        super(name, defaultValue);
    }
}
