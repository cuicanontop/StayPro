package dev.cuican.staypro.event.events.render;

import dev.cuican.staypro.event.StayEvent;

public final class HudOverlayEvent extends StayEvent {

    private final Type type;

    public HudOverlayEvent(Type type) {
        this.type = type;
    }

    public final Type getType() {
        return this.type;
    }

    public enum Type {
        WATER,
        LAVA,
        PUMPKIN,
        HURTCAM,
        SCOREBOARD,
        FIRE,
        STAT_ALL,
        STAT_HEALTH,
        STAT_FOOD,
        STAT_ARMOR,
        STAT_AIR,
        BOSS_BAR,
        EXP_BAR,
        VIGNETTE,
        CROSSHAIR,
        ATTACK_INDICATOR,
        JUMP_BAR,
        MOUNT_HEALTH,
        PORTAL,
        SELECTED_ITEM_TOOLTIP,
        POTION_EFFECTS
    }

}
