package dev.cuican.staypro.module.modules.render;

import dev.cuican.staypro.client.FriendManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.ColorUtil;
import dev.cuican.staypro.utils.graphics.RenderUtils3D;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

@ModuleInfo(name = "BurrowEsp", category = Category.RENDER,description = "Burrowesp")
public class Burrowesp extends Module {

    public Setting<Boolean> rainbow = setting("Rainbow", false);
    private final Setting<Integer> range = setting("Range", 10, 0, 20);
    public Setting<Boolean> box = setting("Box", true);
    public Setting<Boolean> gradientBox = setting("Gradient", Boolean.FALSE).whenTrue(box);
    public Setting<Boolean> invertGradientBox = setting("ReverseGradient", Boolean.FALSE).whenTrue(gradientBox);
    public Setting<Boolean> outline = setting("Outline", true);
    public Setting<Boolean> gradientOutline = setting("GradientOutline", Boolean.FALSE).whenTrue(outline);
    public Setting<Boolean> invertGradientOutline = setting("ReverseOutline", Boolean.FALSE).whenTrue(gradientOutline);
    public Setting<Double> height = setting("Height", 0.0, -2.0, 2.0);

    private final Setting<Integer> boxAlpha = setting("BoxAlpha", 125, 0, 255).whenTrue(box);
    private final Setting<Float> lineWidth = setting("LineWidth", 1.0f, 0.1f, 5.0f).whenTrue(outline);
    public Setting<Boolean> safeColor = setting("BedrockColor", false);
    private final Setting<Integer> safeRed = setting("BedrockRed", 0, 0, 255).whenTrue(safeColor);
    private final Setting<Integer> safeGreen = setting("BedrockGreen", 255, 0, 255).whenTrue(safeColor);
    private final Setting<Integer> safeBlue = setting("BedrockBlue", 0, 0, 255).whenTrue(safeColor);
    private final Setting<Integer> safeAlpha = setting("BedrockAlpha", 255, 0, 255).whenTrue(safeColor);
    public Setting<Boolean> customOutline = setting("CustomLine", Boolean.FALSE).whenTrue(outline);
    private final Setting<Integer> safecRed = setting("OL-SafeRed", 0, 0, 255).whenTrue(customOutline).whenTrue(outline).whenTrue(safeColor);
    private final Setting<Integer> safecGreen = setting("OL-SafeGreen", 255, 0, 255).whenTrue(customOutline).whenTrue(outline).whenTrue(safeColor);
    private final Setting<Integer> safecBlue = setting("OL-SafeBlue", 0, 0, 255).whenTrue(customOutline).whenTrue(outline).whenTrue(safeColor);
    private final Setting<Integer> safecAlpha = setting("OL-SafeAlpha", 255, 0, 255).whenTrue(customOutline).whenTrue(outline).whenTrue(safeColor);




    public EntityPlayer find_closest_target(double range) {
        if (mc.world.playerEntities.isEmpty()) {
            return null;
        }
        EntityPlayer closestTarget = null;
        for (EntityPlayer target : mc.world.playerEntities) {
            if (target == mc.player)
                continue;
            if(mc.player.getDistance(target)>range)
                continue;
            if (FriendManager.isFriend(target.getName()))
                continue;
            if (target.getHealth() <= 0.0f)
                continue;
            if (closestTarget != null)
                if (mc.player.getDistance(target) > mc.player.getDistance(closestTarget))
                    continue;
            closestTarget = target;
        }
        return closestTarget;
    }
    @Override
    public void onRenderWorld(RenderEvent event) {
        EntityPlayer pss=  find_closest_target(range.getValue());
        if(pss!=null){

            if(mc.world.getBlockState(new BlockPos(pss.posX,pss.posY,pss.posZ)).getBlock()== Blocks.AIR||mc.world.getBlockState(new BlockPos(pss.posX,pss.posY,pss.posZ)).getBlock()== Blocks.WATER||mc.world.getBlockState(new BlockPos(pss.posX,pss.posY,pss.posZ)).getBlock()== Blocks.LAVA|| mc.world.getBlockState(new BlockPos(pss.posX,pss.posY,pss.posZ)).getBlock()== Blocks.GRASS){
                return;
            }

            RenderUtils3D.drawBoxESP(new BlockPos(pss.posX,pss.posY,pss.posZ), rainbow.getValue() ? ColorUtil.rainbow(GUIManager.getColor3I()) : new Color(safeRed.getValue(), safeGreen.getValue(), safeBlue.getValue(), safeAlpha.getValue()), customOutline.getValue(), new Color(safecRed.getValue(), safecGreen.getValue(), safecBlue.getValue(), safecAlpha.getValue()), lineWidth.getValue(), outline.getValue(), box.getValue(), boxAlpha.getValue(), true, height.getValue(), gradientBox.getValue(), gradientOutline.getValue(), invertGradientBox.getValue(), invertGradientOutline.getValue(), 0);
        }

    }
}
