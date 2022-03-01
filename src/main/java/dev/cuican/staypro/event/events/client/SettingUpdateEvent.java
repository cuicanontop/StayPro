package dev.cuican.staypro.event.events.client;

import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.event.StayEvent;

public class SettingUpdateEvent extends StayEvent {

    private final Setting<?> setting;

    public SettingUpdateEvent(Setting<?> setting) {
        this.setting = setting;
    }

    public Setting<?> getSetting() {
        return setting;
    }
}
