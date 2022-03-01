package dev.cuican.staypro.module.modules.render;



import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.utils.Timer;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.RenderUtil;
import dev.cuican.staypro.utils.RotationUtil;
import dev.cuican.staypro.utils.block.BlockUtil;
import dev.cuican.staypro.utils.graphics.RenderUtils3D;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
@ModuleInfo(name = "VoidEsp", category = Category.RENDER,description = "Esps the void")
public class VoidESP
        extends Module {
    private final Setting<Float> radius = setting("Radius", 8.0f, 0.0f, 50.0f);
    private final Timer timer = new Timer();
    public Setting<Boolean> air = setting("OnlyAir", true);
    public Setting<Boolean> noEnd = setting("NoEnd", true);
    public Setting<Boolean> box = setting("Box", true);
    public Setting<Boolean> outline = setting("Outline", true);
    public Setting<Boolean> colorSync = setting("Sync", false);
    public Setting<Double> height = setting("Height", 0.0, -2.0, 2.0);
    public Setting<Boolean> customOutline = setting("CustomLine", false).whenTrue(outline);
    private final Setting<Integer> updates = setting("Updates", 500, 0, 1000);
    private final Setting<Integer> voidCap = setting("VoidCap", 500, 0, 1000);
    private final Setting<Integer> red = setting("Red", 0, 0, 255);
    private final Setting<Integer> green = setting("Green", 255, 0, 255);
    private final Setting<Integer> blue = setting("Blue", 0, 0, 255);
    private final Setting<Integer> alpha = setting("Alpha", 255, 0, 255);
    private final Setting<Integer> boxAlpha = setting("BoxAlpha", 125, 0, 255).whenTrue(box);
    private final Setting<Float> lineWidth = setting("LineWidth", 1.0f, 0.1f, 5.0f).whenTrue(outline);
    private final Setting<Integer> cRed = setting("OL-Red", 0, 0, 255).whenTrue(customOutline).whenTrue(this.outline);
    private final Setting<Integer> cGreen = setting("OL-Green", 0, 0, 255).whenTrue(this.outline).whenTrue(this.customOutline);
    private final Setting<Integer> cBlue = setting("OL-Blue", 255, 0, Integer.valueOf(255)).whenTrue(this.customOutline).whenTrue(this.outline);
    private final Setting<Integer> cAlpha = setting("OL-Alpha", 255, 0, 255).whenTrue(this.customOutline).whenTrue(this.outline);
    private List<BlockPos> voidHoles = new CopyOnWriteArrayList<BlockPos>();




    @Override
    public void onTick() {
        if (!(VoidESP.fullNullCheck() || this.noEnd.getValue() && VoidESP.mc.player.dimension == 1 || !this.timer.passedMs(this.updates.getValue()))) {
            this.voidHoles.clear();
            this.voidHoles = this.findVoidHoles();
            if (this.voidHoles.size() > this.voidCap.getValue()) {
                this.voidHoles.clear();
            }
            this.timer.reset();
        }
    }

    @Override
    public void onRenderWorld(RenderEvent event) {
        if (VoidESP.fullNullCheck() || this.noEnd.getValue().booleanValue() && VoidESP.mc.player.dimension == 1) {
            return;
        }
        for (BlockPos pos : this.voidHoles) {
            if (!RotationUtil.isInFov(pos)) continue;
            RenderUtils3D.drawBoxESP(pos, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.customOutline.getValue(), new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), true, this.height.getValue(), false, false, false, false, 0);
        }
    }

    private List<BlockPos> findVoidHoles() {
        BlockPos playerPos = EntityUtil.getPlayerPos(VoidESP.mc.player);
        return BlockUtil.getDisc(playerPos.add(0, -playerPos.getY(), 0), this.radius.getValue().floatValue()).stream().filter(this::isVoid).collect(Collectors.toList());
    }

    private boolean isVoid(BlockPos pos) {
        return (VoidESP.mc.world.getBlockState(pos).getBlock() == Blocks.AIR || this.air.getValue() == false && VoidESP.mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK) && pos.getY() < 1 && pos.getY() >= 0;
    }
}

