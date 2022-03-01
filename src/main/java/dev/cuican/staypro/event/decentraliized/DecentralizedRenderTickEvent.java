package dev.cuican.staypro.event.decentraliized;

import dev.cuican.staypro.concurrent.decentralization.DecentralizedEvent;
import dev.cuican.staypro.event.events.render.RenderOverlayEvent;

public class DecentralizedRenderTickEvent extends DecentralizedEvent<RenderOverlayEvent> {
    public static DecentralizedRenderTickEvent instance = new DecentralizedRenderTickEvent();
}
