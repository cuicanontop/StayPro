/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.AxisAlignedBB
 */
package dev.cuican.staypro.event.events.client;


import net.minecraft.util.math.AxisAlignedBB;


public final class LandStepEvent {

    private final AxisAlignedBB bb;
    private float stepHeight;

    public LandStepEvent( AxisAlignedBB bb, float stepHeight) {
        this.bb = bb;
        this.stepHeight = stepHeight;
    }


    public final AxisAlignedBB getBb() {
        return this.bb;
    }

    public final float getStepHeight() {
        return this.stepHeight;
    }

    public final void setStepHeight(float f) {
        this.stepHeight = f;
    }


    public final AxisAlignedBB component1() {
        return this.bb;
    }

    public final float component2() {
        return this.stepHeight;
    }


    public final LandStepEvent copy( AxisAlignedBB bb, float stepHeight) {

        return new LandStepEvent(bb, stepHeight);
    }

    public static /* synthetic */ LandStepEvent copy$default(LandStepEvent landStepEvent, AxisAlignedBB axisAlignedBB, float f, int n, Object object) {
        if ((n & 1) != 0) {
            axisAlignedBB = landStepEvent.bb;
        }
        if ((n & 2) == 0) return landStepEvent.copy(axisAlignedBB, f);
        f = landStepEvent.stepHeight;
        return landStepEvent.copy(axisAlignedBB, f);
    }


    public String toString() {
        return "LandStepEvent(bb=" + this.bb + ", stepHeight=" + this.stepHeight + ')';
    }

    public int hashCode() {
        int result2 = this.bb.hashCode();
        return result2 * 31 + Float.hashCode(this.stepHeight);
    }

    public boolean equals( Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LandStepEvent)) {
            return false;
        }
        LandStepEvent landStepEvent = (LandStepEvent)other;
        if (!this.bb.equals( landStepEvent.bb)) {
            return false;
        }

        if (this.stepHeight==landStepEvent.stepHeight) return true;
        return false;
    }
}

