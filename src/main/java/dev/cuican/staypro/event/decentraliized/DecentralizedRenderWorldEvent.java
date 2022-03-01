package dev.cuican.staypro.event.decentraliized;

import dev.cuican.staypro.concurrent.decentralization.DecentralizedEvent;
import dev.cuican.staypro.event.events.render.RenderWorldEvent;

public class DecentralizedRenderWorldEvent extends DecentralizedEvent<RenderWorldEvent> {
    public static DecentralizedRenderWorldEvent instance = new DecentralizedRenderWorldEvent();
}
