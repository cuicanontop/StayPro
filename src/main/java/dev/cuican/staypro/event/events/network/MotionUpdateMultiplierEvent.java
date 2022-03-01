/*
 * Decompiled with CFR 0.151.
 */
package dev.cuican.staypro.event.events.network;



public final class MotionUpdateMultiplierEvent {
    private int factor = 1;

    public final int getFactor() {
        return this.factor;
    }

    public final void setFactor(int n) {
        this.factor = n;
    }
}

