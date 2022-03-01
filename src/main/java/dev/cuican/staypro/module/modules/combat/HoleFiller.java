package dev.cuican.staypro.module.modules.combat;


import dev.cuican.staypro.client.FriendManager;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.HoleUtil;
import dev.cuican.staypro.utils.block.BlockUtil;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import dev.cuican.staypro.utils.look;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(name = "HoleFiller", description = "Auto Hole Filling", category = Category.COMBAT)
public class HoleFiller extends Module {

    public final Setting<Integer> range = setting("Range", 4, 1, 6);
    public final Setting<Boolean> rotate = setting("Rotate", true);
    public final Setting<Boolean> packet = setting("Packet", false);
    public final Setting<Boolean> Double = setting("Double", false);
    public final Setting<Boolean> Four = setting("Four", false);
    public BlockPos render;
    public EntityPlayer closestTarget;
    public int slot = -1;



    public BlockPos getPlayerPos() {
        return new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
    }


    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        slot = mc.player.inventory.currentItem;
    }

    @Override
    public void onRenderTick() {
        if (fullNullCheck()) {
            return;
        }

        findClosestTarget();
        List<BlockPos> blocks = findCrystalBlocks();
        List<BlockPos> blocks2 = findCrystalBlocks2();
        BlockPos q = null;
        for (BlockPos blockPos : blocks) {
            if (mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos)).isEmpty())
                if (isInRange(blockPos)) {
                    q = blockPos;
                }
            if (rotate.getValue()) {
                look.lookAt(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                       }
        }
        for (BlockPos blockPos : blocks2) {
            if (mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos)).isEmpty())
                if (isInRange(blockPos)) {
                    q = blockPos;
                }
        }
        render = q;
        if (render != null ) {
            //BlockInteractionHelper.placeBlockScaffold(render);
            int i  = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN.getClass());
            if( i== -1){
                return;
            }
            slot = mc.player.inventory.currentItem;
            InventoryUtil.switchToSlot(i);
            if (rotate.getValue()) {
                look.lookAt(new Vec3d(render.getX(), render.getY(), render.getZ()));
            }
            BlockUtil.placeBlock(render, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), mc.player.isSneaking());
            InventoryUtil.switchToSlot(slot);
        }
    }



    public boolean IsHole(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0.5, 0.5, 0.5);
        HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(boost, false, false);
        HoleUtil.HoleType holeType = holeInfo.getType();
        if(holeType!= HoleUtil.HoleType.NONE&&mc.world.getBlockState(boost).getBlock() == Blocks.AIR){
            return (holeType== HoleUtil.HoleType.SINGLE)
                    ||(Double.getValue() && holeType == HoleUtil.HoleType.DOUBLE);
        }else {
            if(Four.getValue()){
                return IsHole4(blockPos);
            }
        }
        return false;
    }

    public boolean IsHole4(BlockPos blockPos){

        BlockPos doubleHole1 = blockPos.add(0, 0, 1);
        BlockPos doubleHole2 = blockPos.add(1, 0, 0);
        BlockPos doubleHole3 = blockPos.add(1, 0, 1);

        BlockPos doubleHole4 = blockPos.add(0, -1, 0);
        BlockPos doubleHole5 = blockPos.add(0, -1, 1);
        BlockPos doubleHole6 = blockPos.add(1, -1, 0);
        BlockPos doubleHole7 = blockPos.add(1, -1, 1);

        BlockPos doubleHole8 = blockPos.add(0, 0, -1);
        BlockPos doubleHole9 = blockPos.add(-1, 0, 0);
        BlockPos doubleHole10 = blockPos.add(0, 0, 2);
        BlockPos doubleHole11 = blockPos.add(-1, 0, 1);
        BlockPos doubleHole12 = blockPos.add(1, 0, -1);
        BlockPos doubleHole13 = blockPos.add(2, 0, 0);
        BlockPos doubleHole14 = blockPos.add(2, 0, 1);
        BlockPos doubleHole15 = blockPos.add(1, 0, 2);

        return (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR)
                &&(mc.world.getBlockState(doubleHole1).getBlock() == Blocks.AIR)
                &&(mc.world.getBlockState(doubleHole2).getBlock() == Blocks.AIR)
                &&(mc.world.getBlockState(doubleHole3).getBlock() == Blocks.AIR)

                &&ISbos(doubleHole4)
                &&ISbos(doubleHole5)
                &&ISbos(doubleHole6)
                &&ISbos(doubleHole7)
                &&ISbos(doubleHole8)
                &&ISbos(doubleHole9)
                &&ISbos(doubleHole10)
                &&ISbos(doubleHole11)
                &&ISbos(doubleHole12)
                &&ISbos(doubleHole13)
                &&ISbos(doubleHole14)
                &&ISbos(doubleHole15);
    }

    public boolean ISbos(BlockPos blockPos){
        return mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN ||mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK;
    }

    public boolean IsHole2(BlockPos blockPos) {
        BlockPos doubleHole1 = blockPos.add(0, 0, -2);
        BlockPos doubleHole2 = blockPos.add(0, 0, 2);
        BlockPos doubleHole3 = blockPos.add(-2, 0, 0);
        BlockPos doubleHole4 = blockPos.add(2, 0, 0);
        return ((mc.world.getBlockState(doubleHole1).getBlock() == Blocks.OBSIDIAN) || (mc.world.getBlockState(doubleHole1).getBlock() == Blocks.BEDROCK))
                && ((mc.world.getBlockState(doubleHole2).getBlock() == Blocks.OBSIDIAN) || (mc.world.getBlockState(doubleHole2).getBlock() == Blocks.BEDROCK))
                && ((mc.world.getBlockState(doubleHole3).getBlock() == Blocks.OBSIDIAN) || (mc.world.getBlockState(doubleHole3).getBlock() == Blocks.BEDROCK))
                && ((mc.world.getBlockState(doubleHole4).getBlock() == Blocks.OBSIDIAN) || (mc.world.getBlockState(doubleHole4).getBlock() == Blocks.BEDROCK));
    }

    public BlockPos getClosestTargetPos() {
        if (closestTarget != null) {
            return new BlockPos(Math.floor(closestTarget.posX), Math.floor(closestTarget.posY), Math.floor(closestTarget.posZ));
        } else {
            return null;
        }
    }


    public void findClosestTarget() {
        List<EntityPlayer> playerList = mc.world.playerEntities;
        closestTarget = null;
        for (EntityPlayer target : playerList) {
            if (target == mc.player) {
                continue;
            }
            if (FriendManager.isFriend(target.getName())) {
                continue;
            }
            if (!EntityUtil.isLiving(target)) {
                continue;
            }
            if ((target).getHealth() <= 0) {
                continue;
            }
            if (closestTarget == null) {
                closestTarget = target;
                continue;
            }
            if (mc.player.getDistance(target) < mc.player.getDistance(closestTarget)) {
                closestTarget = target;
            }
        }
    }

    public boolean isInRange(BlockPos blockPos) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(
                getSphere(getPlayerPos(), range.getValue().floatValue(), range.getValue(), false, true, 0)
                        .stream().filter(this::IsHole).collect(Collectors.toList()));
        return positions.contains(blockPos);
    }

    public List<BlockPos> findCrystalBlocks() {
        NonNullList<BlockPos> positions = NonNullList.create();
        if (closestTarget != null) {
            positions.addAll(
                    getSphere(getClosestTargetPos(), range.getValue(), range.getValue(), false, true, 0)
                            .stream().filter(this::IsHole).filter(this::isInRange).collect(Collectors.toList()));
        }
        return positions;
    }

    public boolean isInRange2(BlockPos blockPos) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(
                getSphere(getPlayerPos(), range.getValue().floatValue(), range.getValue(), false, true, 0)
                        .stream().filter(this::IsHole2).collect(Collectors.toList()));
        return positions.contains(blockPos);
    }

    public List<BlockPos> findCrystalBlocks2() {
        NonNullList<BlockPos> positions = NonNullList.create();
        if (closestTarget != null) {
            positions.addAll(
                    getSphere(getClosestTargetPos(), range.getValue(), range.getValue(), false, true, 0)
                            .stream().filter(this::IsHole2).filter(this::isInRange2).collect(Collectors.toList()));
        }
        return positions;
    }

    public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }
        closestTarget = null;
        render = null;

    }
}