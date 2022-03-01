package dev.cuican.staypro.event.events.client;

import dev.cuican.staypro.event.StayEvent;
import net.minecraft.entity.player.EntityPlayer;

public class TotemPopEvent
        extends StayEvent {
    private final EntityPlayer entity;

    public TotemPopEvent(EntityPlayer entity) {
        this.entity = entity;
    }

    public EntityPlayer getEntity() {
        return this.entity;
    }
}

