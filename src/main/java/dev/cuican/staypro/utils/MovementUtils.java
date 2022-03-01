package dev.cuican.staypro.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInput;
import org.jetbrains.annotations.NotNull;

public class MovementUtils {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static final double getSpeed(Entity speed) {

        double d = speed.motionX;
        double d2 = speed.motionZ;
        return Math.hypot(d, d2);
    }
    public static final boolean isMoving() {
        if (mc.player.movementInput.moveForward != 0.0f) return true;
        return mc.player.movementInput.moveStrafe != 0.0f;
    }
    public static final void resetMove(MovementInput $this$resetMove) {
        $this$resetMove.moveForward = 0.0f;
        $this$resetMove.moveStrafe = 0.0f;
        $this$resetMove.forwardKeyDown = false;
        $this$resetMove.backKeyDown = false;
        $this$resetMove.leftKeyDown = false;
        $this$resetMove.rightKeyDown = false;
    }
}
