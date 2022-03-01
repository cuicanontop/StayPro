/*
 * Decompiled with CFR 0.151.
 */
package dev.cuican.staypro.event.events.client;

import dev.cuican.staypro.event.StayEvent;

public class StepEvent
extends StayEvent {

    private float height;

    public StepEvent(float height) {
        this.height = height;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

}

