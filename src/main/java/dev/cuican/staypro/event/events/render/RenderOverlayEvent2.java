package dev.cuican.staypro.event.events.render;


import dev.cuican.staypro.event.StayEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public final class RenderOverlayEvent2
        extends StayEvent {

    private OverlayType type;


    public RenderOverlayEvent2(final int stage, OverlayType type) {
        super(stage);
        this.type = type;
    }

    public RenderOverlayEvent2(OverlayType type) {
        this.type = type;
    }

    public static /* synthetic */ RenderOverlayEvent2 copy$default(RenderOverlayEvent2 renderOverlayEvent, OverlayType overlayType, int n, Object object) {
        if ((n & 1) != 0) {
            overlayType = renderOverlayEvent.type;
        }
        return renderOverlayEvent.copy(overlayType);
    }

    public final OverlayType getType() {
        return this.type;
    }

    public final void setType(OverlayType overlayType) {
        this.type = overlayType;
    }

    public final OverlayType component1() {
        return this.type;
    }

    public final RenderOverlayEvent2 copy(OverlayType type) {
        return new RenderOverlayEvent2(type);
    }

    public String toString() {
        return "RenderOverlayEvent2(type=" + this.type + ")";
    }

    public int hashCode() {
        OverlayType overlayType = this.type;
        return overlayType != null ? ((Object) overlayType).hashCode() : 0;
    }

    public boolean equals(Object object) {
        block3:
        {
            block2:
            {
                if (this == object) break block2;
                if (!(object instanceof RenderOverlayEvent2)) break block3;
                RenderOverlayEvent2 renderOverlayEvent = (RenderOverlayEvent2) object;
            }
            return true;
        }
        return false;
    }

    public enum OverlayType {
        BLOCK,
        LIQUID,
        FIRE
    }
}

