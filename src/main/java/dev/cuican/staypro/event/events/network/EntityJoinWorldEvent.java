package dev.cuican.staypro.event.events.network;

import dev.cuican.staypro.event.StayEvent;
import net.minecraft.entity.Entity;

import java.util.Collection;

public class EntityJoinWorldEvent extends StayEvent {

    private final Entity entity;

    public EntityJoinWorldEvent(int s,Collection<Entity> entity)
    {
        super(s);
        this.entity = (Entity) entity;

    }

    public Entity getEntity()
    {
        return entity;
    }
}
