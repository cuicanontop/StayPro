package dev.cuican.staypro.utils;


import dev.cuican.staypro.mixin.accessor.AccessorMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class Wrapper {

    public static Minecraft mc = Minecraft.getMinecraft();

    public static EntityPlayerSP getPlayer() {
        return Wrapper.getMinecraft().player;
    }

    public static Minecraft getMinecraft() {
        return mc;
    }

    public static World getWorld() {
        return Wrapper.getMinecraft().world;
    }

    public static int getKey(String keyname) {
        return Keyboard.getKeyIndex((String)keyname.toUpperCase());
    }
    public static net.minecraft.util.Timer getTimer() {
        return ((AccessorMinecraft) Minecraft.getMinecraft()).aqGetTimer();
    }

}

