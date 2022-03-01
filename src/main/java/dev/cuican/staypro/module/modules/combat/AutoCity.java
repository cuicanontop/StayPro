package dev.cuican.staypro.module.modules.combat;


import dev.cuican.staypro.client.FriendManager;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.module.modules.player.Instant;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(name = "AutoCity", category = Category.COMBAT, description = "AutoCity")
public class AutoCity extends Module {
    public final Setting<Integer> targetRange = setting("Target Range", 7, 0, 16);
    private final Setting<Boolean> disable =setting ("disable", true);
    private final Setting<Boolean> cev =setting ("On Cev", true);
    public EntityPlayer target;
    @Override
    public void onTick() {
        if (fullNullCheck()) {
            return;
        }
        if(cev.getValue()&&ModuleManager.getModuleByName("CevBreaker").isEnabled()){
            return;
        }

        if(disable.getValue()){
            disable();
        }
        if (InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) == -1) {
            return;
        }
        this.target = this.find_closest_target(targetRange.getValue());
        this.surroundMine(target);
    }

    @Override
    public String getModuleInfo() {
        if (this.target == null) {
            return "NoTarget";
        }
        return this.target.getName();
    }

    private void surroundMine(EntityPlayer as) {
        if (as == null) {
            return;
        }
        Vec3d a = as.getPositionVector();
        if (EntityUtil.getSurroundWeakness(a, 1, -1)) {
            this.surroundMine(a, -1.0, 0.0, 0.0);
            return;
        }
        if (EntityUtil.getSurroundWeakness(a, 2, -1)) {
            this.surroundMine(a, 1.0, 0.0, 0.0);
            return;
        }
        if (EntityUtil.getSurroundWeakness(a, 3, -1)) {
            this.surroundMine(a, 0.0, 0.0, -1.0);
            return;
        }
        if (EntityUtil.getSurroundWeakness(a, 4, -1)) {
            this.surroundMine(a, 0.0, 0.0, 1.0);
            return;
        }
        if (EntityUtil.getSurroundWeakness(a, 5, -1)) {
            this.surroundMine(a, -1.0, 0.0, 0.0);
            return;
        }
        if (EntityUtil.getSurroundWeakness(a, 6, -1)) {
            this.surroundMine(a, 1.0, 0.0, 0.0);
            return;
        }
        if (EntityUtil.getSurroundWeakness(a, 7, -1)) {
            this.surroundMine(a, 0.0, 0.0, -1.0);
            return;
        }
        if (EntityUtil.getSurroundWeakness(a, 8, -1)) {
            this.surroundMine(a, 0.0, 0.0, 1.0);
        }

    }

    private void surroundMine(Vec3d pos, double x, double y, double z) {
        BlockPos position = new BlockPos(pos).add(x, y, z);
        if(Instant.breakPos!=null){
            if(Instant.breakPos.equals(position))return;
            if(mc.world.getBlockState(new BlockPos(target.posX,target.posY,target.posZ)).getBlock()!=Blocks.AIR) return;

        }
        Instant.ondeve(position);

    }
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


}
