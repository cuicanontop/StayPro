package dev.cuican.staypro.event.events.render;

import dev.cuican.staypro.concurrent.decentralization.EventData;
import dev.cuican.staypro.event.StayEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public final class RenderOverlayEvent extends StayEvent implements EventData {

    private final float partialTicks;
    private final ScaledResolution scaledResolution;

    public RenderOverlayEvent() {
        this.partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        this.scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
    }

    public RenderOverlayEvent(float partialTicks) {
        this.partialTicks = partialTicks;
        this.scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
    }

    public final float getPartialTicks() {
        return partialTicks;
    }

    public final ScaledResolution getScaledResolution() {
        return this.scaledResolution;
    }

}
