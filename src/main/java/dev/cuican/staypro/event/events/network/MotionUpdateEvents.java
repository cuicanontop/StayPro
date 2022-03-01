package dev.cuican.staypro.event.events.network;


import dev.cuican.staypro.event.StayEvent;
import dev.cuican.staypro.module.pingbypass.util.Globals;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;


@SuppressWarnings("unused")
public class MotionUpdateEvents extends StayEvent implements Globals
{
    private double x;
    private double y;
    private double z;
    private float rotationYaw;
    private float rotationPitch;
    private boolean onGround;
    protected boolean modified;

    public MotionUpdateEvents( MotionUpdateEvents event)
    {
        this(
             event.x,
             event.y,
             event.z,
             event.rotationYaw,
             event.rotationPitch,
             event.onGround);
    }

    public MotionUpdateEvents(
                             double x,
                             double y,
                             double z,
                             float rotationYaw,
                             float rotationPitch,
                             boolean onGround)
    {

        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.onGround = onGround;
    }

    public boolean isModified()
    {
        return modified;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.modified = true;
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.modified = true;
        this.y = y;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.modified = true;
        this.z = z;
    }

    public float getYaw()
    {
        return rotationYaw;
    }

    public void setYaw(float rotationYaw)
    {
        this.modified = true;
        this.rotationYaw = rotationYaw;
    }

    public float getPitch()
    {
        return rotationPitch;
    }

    public void setPitch(float rotationPitch)
    {
        this.modified = true;
        this.rotationPitch = rotationPitch;
    }

    public boolean isOnGround()
    {
        return onGround;
    }

    public void setOnGround(boolean onGround)
    {
        this.modified = true;
        this.onGround = onGround;
    }

    /**
     * Fired in {@link EntityPlayerSP#onUpdate()}, when the player
     * is riding. X, Y, and Z can be set but it won't have any effect.
     * You can however retrieve the ridden Entity with
     * {@link Riding#getEntity()} and set its x, y, and z.
     */
    public static class Riding extends MotionUpdateEvents
    {
        private float moveStrafing;
        private float moveForward;
        private boolean jump;
        private boolean sneak;

        public Riding(
                      double x,
                      double y,
                      double z,
                      float rotationYaw,
                      float rotationPitch,
                      boolean onGround,
                      float moveStrafing,
                      float moveForward,
                      boolean jump,
                      boolean sneak)
        {
            super( x, y, z, rotationYaw, rotationPitch, onGround);
            this.moveStrafing = moveStrafing;
            this.moveForward = moveForward;
            this.jump = jump;
            this.sneak = sneak;
        }

        public Riding( Riding event)
        {
            this(
                 event.getX(),
                 event.getY(),
                 event.getZ(),
                 event.getYaw(),
                 event.getPitch(),
                 event.isOnGround(),
                 event.moveStrafing,
                 event.moveForward,
                 event.jump,
                 event.sneak);
        }

        public Entity getEntity()
        {
            return mc.player.getLowestRidingEntity();
        }

        public float getMoveStrafing()
        {
            return moveStrafing;
        }

        public void setMoveStrafing(float moveStrafing)
        {
            this.modified = true;
            this.moveStrafing = moveStrafing;
        }

        public float getMoveForward()
        {
            return moveForward;
        }

        public void setMoveForward(float moveForward)
        {
            this.modified = true;
            this.moveForward = moveForward;
        }

        public boolean getJump()
        {
            return jump;
        }

        public void setJump(boolean jump)
        {
            this.modified = true;
            this.jump = jump;
        }

        public boolean getSneak()
        {
            return sneak;
        }

        public void setSneak(boolean sneak)
        {
            this.modified = true;
            this.sneak = sneak;
        }
    }

}
