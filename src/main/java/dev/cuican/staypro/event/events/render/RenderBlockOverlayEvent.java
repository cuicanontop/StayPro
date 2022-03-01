package dev.cuican.staypro.event.events.render;

import dev.cuican.staypro.event.StayEvent;

import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class RenderBlockOverlayEvent extends StayEvent {
    private float partialTicks;

    public RenderBlockOverlayEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }
    public float getPartialTicks(){
        return this.partialTicks;
    }
}
