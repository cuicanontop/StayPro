package dev.cuican.staypro.module.modules.render;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.graphics.StayTessellator;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.awt.*;

@ModuleInfo(name="BlockHighlight", category= Category.RENDER,description = "BlockHighlight")
public class BlockHighlight
extends Module {
    private final Setting<Boolean> boundingbox = setting("BoundingBox", true);
    private final Setting<Boolean> box = setting("FullBlock", true);
    private final Setting<Float> width = setting("Width", 1.5f, 0.0f, 10.0f);
    private final Setting<Integer> alpha = setting("Alpha", 28, 1, 255);
    private final Setting<Integer> Red = setting("Red", 255, 1, 255);
    private final Setting<Integer> Green = setting("Green", 255, 1, 255);
    private final Setting<Integer> Blue = setting("Blue", 255, 1, 255);
    private final Setting<Integer> alpha2 = setting("Alpha", 255, 1, 255);
    private final Setting<Boolean> rainbow = setting("Rainbow", true);

    @Listener
    public void onRenderWorld(RenderEvent event) {
        BlockPos blockpos;
        float[] hue = new float[]{(float)(System.currentTimeMillis() % 11520L) / 11520.0f};
        int rgb = Color.HSBtoRGB(hue[0], 1.0f, 1.0f);
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        RayTraceResult ray = mc.objectMouseOver;
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK && mc.world.getBlockState(blockpos = ray.getBlockPos()).getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
            if (this.box.getValue()) {
                StayTessellator.prepare(7);
                if (this.rainbow.getValue()) {
                    StayTessellator.drawBox(blockpos, r, g, b, this.alpha.getValue(), 63,1);
                } else {
                    StayTessellator.drawBox(blockpos, this.Red.getValue(), this.Green.getValue(), this.Blue.getValue(), this.alpha.getValue(), 63);
                }
                StayTessellator.release();
            }
            if (this.boundingbox.getValue()) {
                StayTessellator.prepare(7);
                if (this.rainbow.getValue()) {
                    StayTessellator.drawBoundingBoxBlockPos(blockpos, this.width.getValue(), r, g, b, this.alpha2.getValue());
                } else {
                    StayTessellator.drawBoundingBoxBlockPos(blockpos, this.width.getValue(), this.Red.getValue(), this.Green.getValue(), this.Blue.getValue(), this.alpha2.getValue());
                }
                StayTessellator.release();
            }
        }
    }
}

