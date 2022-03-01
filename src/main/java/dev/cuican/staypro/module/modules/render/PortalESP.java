package dev.cuican.staypro.module.modules.render;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.RenderUtil;
import dev.cuican.staypro.utils.graphics.RenderUtils3D;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayList;

@ModuleInfo(name = "PortalESP", category = Category.RENDER, description = "PortalEsp")
public class PortalESP
        extends Module {
    private final ArrayList<BlockPos> blockPosArrayList = new ArrayList<>();
    private final Setting<Integer> distance = setting("Range", 50, 1, 100);
    private final Setting<Boolean> box = setting("Box", true);
    private final Setting<Integer> boxAlpha = setting("BoxAlpha", 150, 0, 255).whenTrue(box);
    private final Setting<Boolean> outline = setting("Outline", true);
    private final Setting<Float> lineWidth = setting("OutlineWidth", 1f, 0.1f, 5f).whenTrue(outline);
    private int cooldownTicks;

    @Override
    public void onTick() {
        if (mc.world == null) {
            return;
        }
        if (this.cooldownTicks < 1) {
            this.blockPosArrayList.clear();
            this.compileDL();
            this.cooldownTicks = 80;
        }
        --this.cooldownTicks;
    }


    @Override
    public void onRenderWorld(RenderEvent event) {
        if (mc.world == null) {
            return;
        }
        for (BlockPos pos : new ArrayList<>(blockPosArrayList)) {
            RenderUtils3D.drawBoxESP(pos, new Color(0, 197, 204, 255), false, new Color(55, 121, 144, 255), this.lineWidth.getValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
        }
    }

    private void compileDL() {
        if (mc.world == null || mc.player == null) {
            return;
        }
        for (int x = (int) mc.player.posX - this.distance.getValue(); x <= (int) mc.player.posX + this.distance.getValue(); ++x) {
            for (int y = (int) mc.player.posY - this.distance.getValue(); y <= (int) mc.player.posY + this.distance.getValue(); ++y) {
                int z = (int) Math.max(mc.player.posZ - (double) this.distance.getValue(), 0.0);
                while ((double) z <= Math.min(mc.player.posZ + (double) this.distance.getValue(), 255.0)) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (block instanceof BlockPortal) {
                        this.blockPosArrayList.add(pos);
                    }
                    ++z;
                }
            }
        }
    }
}

