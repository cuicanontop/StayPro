package dev.cuican.staypro.event.events;


import dev.cuican.staypro.utils.Wrapper;
import net.minecraftforge.fml.common.eventhandler.Event;

public class MinecraftEvent
extends Event {
    private boolean cancelled;
    public Era era = Era.PRE;
    private final float partialTicks = Wrapper.getMinecraft().getRenderPartialTicks();

    public MinecraftEvent() {
    }

    public MinecraftEvent(Era p_Era) {
        this.era = p_Era;
    }

    public Era getEra() {
        return this.era;
    }

    public final boolean isCancelled() {
        return this.cancelled;
    }

    public final void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public final void cancel() {
        this.setCancelled(true);
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setEra(Era era) {
        this.era = era;
    }

    public static enum Era {
        PRE,
        PERI,
        POST;

    }
}

