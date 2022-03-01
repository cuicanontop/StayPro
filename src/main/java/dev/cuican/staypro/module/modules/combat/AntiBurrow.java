package dev.cuican.staypro.module.modules.combat;


import dev.cuican.staypro.client.FriendManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.module.modules.player.Instant;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.BurrowUtil;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;


@ModuleInfo(name = "AntiBurrow", category = Category.COMBAT, description = "AntiBurrow")
public class AntiBurrow extends Module {
    private final Setting<Double> range = setting("Range", 4.0, 0.0, 10.0);
    private final Setting<Boolean> disable = setting("disable", true);
    public Setting<String> place_mode = setting("Mode", "Digup", listOf(
            "Digup",
            "Piston"
    ));

    public EntityPlayer find_closest_target(double range) {
        if (mc.world.playerEntities.isEmpty()) {
            return null;
        }
        EntityPlayer closestTarget = null;
        for (EntityPlayer target : mc.world.playerEntities) {
            if (target == mc.player)
                continue;
            if (mc.player.getDistance(target) > range)
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
    public static boolean isInsideBlock(EntityPlayer player) {
     return  BurrowUtil.getBurrowValves(new BlockPos(player));
    }
    @Override
    public void onTick() {
        if (fullNullCheck())
            return;
        if (mc.currentScreen instanceof GuiHopper) {
            return;
        }

        EntityPlayer player = find_closest_target(range.getValue());
        if (disable.getValue()) disable();
        if (player == null) return;
        BlockPos pos = new BlockPos(player.posX, player.posY + 0.5D, player.posZ);
        if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR
                && mc.world.getBlockState(pos).getBlock() != Blocks.LAVA
                && mc.world.getBlockState(pos).getBlock() != Blocks.FLOWING_LAVA
                && mc.world.getBlockState(pos).getBlock() != Blocks.FLOWING_WATER
                && mc.world.getBlockState(pos).getBlock() != Blocks.FIRE
                && mc.world.getBlockState(pos).getBlock() != Blocks.WATER
                && mc.world.getBlockState(pos).getBlock() != Blocks.LAVA
                && mc.world.getBlockState(pos).getBlock()!= Blocks.GRASS
                &&isInsideBlock(player)) {
            if(place_mode.getValue().equals("Digup")){
                if (Instant.breakPos != null) {
                    if (Instant.breakPos.getZ() == pos.getZ() && Instant.breakPos.getX() == pos.getX() && Instant.breakPos.getY() == pos.getY()) {
                        return;
                    }
                }
               Instant.ondeve(pos);
            }else {








            }
        }

    }


}
