/*
 * Decompiled with CFR 0.151.
 *
 * Could not load the following classes:
 *  net.minecraft.entity.MoverType
 *  net.minecraftforge.fml.common.eventhandler.Cancelable
 */
package dev.cuican.staypro.event.events.network;

import dev.cuican.staypro.event.StayEvent;
import net.minecraft.entity.MoverType;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class MoveEvent2
        extends StayEvent {
    public MoverType type;
    public double X;
    public double Y;
    public double Z;

    public MoveEvent2( MoverType type, double x, double y, double z) {
        this.type = type;
        this.X = x;
        this.Y = y;
        this.Z = z;

    }

    public MoverType getType() {
        return this.type;
    }

    public void setType(MoverType type) {
        this.type = type;
    }

    public double getX() {
        return this.X;
    }

    public void setX(double x) {
        this.X = x;
    }

    public double getY() {
        return this.Y;
    }

    public void setY(double y) {
        this.Y = y;
    }

    public double getZ() {
        return this.Z;
    }

    public void setZ(double z) {
        this.Z = z;
    }
}

