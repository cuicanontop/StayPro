/*
 * Decompiled with CFR 0.151.
 *
 * Could not load the following classes:
 *  net.minecraft.entity.MoverType
 *  net.minecraftforge.fml.common.eventhandler.Cancelable
 */
package dev.cuican.staypro.event.events.client;

import dev.cuican.staypro.event.StayEvent;
import net.minecraft.entity.MoverType;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class MoveEvent
        extends StayEvent {
    public MoverType type;
    public double X;
    public double Y;
    public double Z;
    private boolean isSneaking;
    public MoveEvent( MoverType type, double x, double y, double z, boolean isSneaking) {
        this.type = type;
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.isSneaking = isSneaking;
    }
    public final boolean isSneaking() {
        return this.isSneaking;
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

