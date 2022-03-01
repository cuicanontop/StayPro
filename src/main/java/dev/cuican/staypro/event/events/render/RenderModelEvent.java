package dev.cuican.staypro.event.events.render;

import dev.cuican.staypro.event.StayEvent;

public class RenderModelEvent extends StayEvent {
    public boolean rotating = false;
    public float pitch = 0;

    public RenderModelEvent(){
        super();
    }
}
