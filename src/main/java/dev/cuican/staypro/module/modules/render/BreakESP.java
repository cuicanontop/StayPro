package dev.cuican.staypro.module.modules.render;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(name = "BreakESP", category = Category.RENDER, description = "BreakEsp")
public class BreakESP extends Module {

    public Setting<String> renderType = setting("Render Mode", "Both",listOf(
            "Outline",
            "Fill",
            "Both"
    ));
    public Setting<String> viewType = setting("View Mode", "InToOut",listOf("OutTOIn", "InToOut"));
    public Setting<Integer> color = setting("Red", 255, 1, 255);
    public Setting<Integer> color2 = setting("Green", 198, 1, 255);
    public Setting<Integer> color3 = setting("Blue", 203, 1, 255);
    public Setting<Integer> alpha = setting("Alpha", 100, 1, 255);
    public Setting<Integer> range = setting("Range", 20, 1, 200);
    public Setting<Integer> lineWidth = setting("LineWidth", 1, 0, 5);
    BreakESPFade fade = new BreakESPFade(200);

    @Listener
    public void onRenderWorld(RenderEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        mc.renderGlobal.damagedBlocks.forEach((integer, destroyBlockProgress) -> {
            if (destroyBlockProgress != null) {
                BlockPos blockPos = destroyBlockProgress.getPosition();
                if (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR) {
                    return;
                }
                if (blockPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= range.getValue()) {
                    int progress = destroyBlockProgress.getPartialBlockDamage();
                    fade.setNewProgress(progress);
                    AxisAlignedBB axisAlignedBB = mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos);
                    renderESP(axisAlignedBB, (float) (viewType.getValue().equals("InToOut") ? fade.getRenderSize() : 1.0 - fade.getRenderSize()), color.getValue(), color2.getValue(), color3.getValue(), alpha.getValue());
                }
            }
        });
    }

    private void renderESP(AxisAlignedBB axisAlignedBB, float progress, int color, int color2, int color3, int alpha) {
        Colors fillColor = new Colors(color, color2, color3, alpha);
        GSColor outlineColor = new GSColor(color, color2, color3, alpha);

        double centerX = axisAlignedBB.minX + ((axisAlignedBB.maxX - axisAlignedBB.minX) / 2);
        double centerY = axisAlignedBB.minY + ((axisAlignedBB.maxY - axisAlignedBB.minY) / 2);
        double centerZ = axisAlignedBB.minZ + ((axisAlignedBB.maxZ - axisAlignedBB.minZ) / 2);
        double full = (axisAlignedBB.maxX - centerX);
        double progressValX = full * progress;
        double progressValY = full * progress;
        double progressValZ = full * progress;

        AxisAlignedBB axisAlignedBB1 = new AxisAlignedBB(centerX - progressValX, centerY - progressValY, centerZ - progressValZ, centerX + progressValX, centerY + progressValY, centerZ + progressValZ);

        switch (renderType.getValue()) {
            case "Fill": {
                RenderUtil.drawBox(axisAlignedBB1, true, 0, new GSColor(fillColor), GeometryMasks.Quad.ALL);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(axisAlignedBB1, lineWidth.getValue(), outlineColor);
                break;
            }
            case "Both": {
                RenderUtil.drawBox(axisAlignedBB1, true, 0, new GSColor(fillColor), GeometryMasks.Quad.ALL);
                RenderUtil.drawBoundingBox(axisAlignedBB1, lineWidth.getValue(), outlineColor);
                break;
            }
        }
    }



    private static class BreakESPFade {
        private final FadeUtils fade;
        private double lastProgress;
        private double newProgress;

        public BreakESPFade(int smoothLength) {
            lastProgress = 0;
            newProgress = 0;
            fade = new FadeUtils(smoothLength);
        }

        public void setNewProgress(double progress) {
            if (progress != newProgress) {
                lastProgress = newProgress;
                newProgress = progress;
                fade.reset();
            }
        }

        public double getRenderSize() {
            if (lastProgress == 0 || newProgress == 0) {
                return 0.0;
            }
            if (newProgress == 10) {
                return 0.0;
            }
            double Nprogress = (1.0 / 10) * newProgress;
            double Lprogress = (1.0 / 10) * lastProgress;
            double maxP = Math.max(Nprogress, Lprogress);
            double minP = Math.min(Nprogress, Lprogress);
            return minP + ((maxP - minP) * fade.easeOutQuad());
        }
    }
}