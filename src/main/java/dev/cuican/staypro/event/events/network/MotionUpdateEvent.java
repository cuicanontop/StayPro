/*
 * Decompiled with CFR 0.151.
 */
package dev.cuican.staypro.event.events.network;


import dev.cuican.staypro.event.StayEvent;
import dev.cuican.staypro.utils.Location;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.NotNull;

public final class MotionUpdateEvent
extends StayEvent {



    private Location location;

    private Vec2f rotation;

    private Vec2f prevRotation;
    private boolean rotating;

    public MotionUpdateEvent(  Location location, Vec2f rotation,Vec2f prevRotation) {
        this.location = location;
        this.rotation = rotation;
        this.prevRotation = prevRotation;
    }


    public final Location getLocation() {

        return this.location;
    }

    public final void setLocation(Location location) {
        this.location = location;
    }

    public final Vec2f getRotation() {
        return this.rotation;
    }

    public final void setRotation(Vec2f vec2f) {
        this.rotation = vec2f;
    }


    public final Vec2f getPrevRotation() {
        return this.prevRotation;
    }

    public final void setPrevRotation(@NotNull Vec2f vec2f) {
        this.prevRotation = vec2f;
    }

    public final boolean getRotating() {
        return this.rotating;
    }

    public final void setRotating(boolean bl) {
        this.rotating = bl;
    }



}

