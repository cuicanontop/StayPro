package dev.cuican.staypro.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;

public class look {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static void lookAt(Vec3d vec3d) {
        float[] v = RotationUtil.getRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), vec3d);
        float[] v2 = RotationUtil.getRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), vec3d.add(0, -0.5, 0));
        setYawAndPitch(v[0], v[1], v2[1]);
    }
    public static void setYawAndPitch(float yaw1, float pitch1, float renderPitch1) {

        mc.player.rotationYawHead = yaw1;
        mc.player.renderYawOffset = yaw1;

    }
}
