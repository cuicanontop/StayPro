package dev.cuican.staypro.event.events.render;

import dev.cuican.staypro.concurrent.decentralization.EventData;
import dev.cuican.staypro.event.StayEvent;

public final class RenderWorldEvent extends StayEvent implements EventData {

    private final float partialTicks;
    private final Pass pass;

    public RenderWorldEvent(float partialTicks, int pass) {
        this.partialTicks = partialTicks;
        this.pass = Pass.values()[pass];
    }

    public final Pass getPass() {
        return this.pass;
    }

    public final float getPartialTicks() {
        return partialTicks;
    }

    public enum Pass {
        ANAGLYPH_CYAN, ANAGLYPH_RED, NORMAL
    }

}
