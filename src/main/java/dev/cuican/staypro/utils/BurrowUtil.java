package dev.cuican.staypro.utils;

import dev.cuican.staypro.utils.block.BlockInteractionHelper;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import dev.cuican.staypro.utils.position.PositionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.stream.Collectors;

public class BurrowUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

     static Timer placeTimer = new Timer();

    public static boolean burrow(boolean rotate, boolean center) {
        if(!placeTimer.passedTick(1))return false;

        if(!mc.player.onGround)return false;
        if( mc.player.isDead)return false;
        BlockPos post = getPlayerPos();
        if (!mc.world.getBlockState(mc.player.getPosition().up(1)).getBlock().equals(Blocks.AIR)) return false;
       if(isInsideBlock()) return false;
        int blockslot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        int is = InventoryUtil.findHotbarBlock(Blocks.ENDER_CHEST);
        int oldSlot = mc.player.inventory.currentItem;
        if (is == -1 && blockslot == -1||oldSlot==-1) return false;
        if (center) {
            setPositionPacket((double) post.getX() + 0.28, post.getY(), post.getZ() + 0.28, true, true);
        }
         EntityUtil.LocalPlayerfakeJump();
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));

        mc.player.setSneaking(true);
        if (blockslot != -1) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(blockslot));
        } else {
            if (is!=-1){
                mc.player.connection.sendPacket(new CPacketHeldItemChange(is));
            }
        }

        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY - 1.0D, mc.player.posZ);
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        mc.player.setSneaking(false);
        mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));


            if (!mc.world.getBlockState(mc.player.getPosition().up(3)).getBlock().equals(Blocks.AIR)){
                  mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,-44,mc.player.posZ,false));

            }else {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,mc.player.posY+ 3.0, mc.player.posZ, false));
            }
        mc.playerController.updateController();
      placeTimer.reset();
        return true;
    }


    public static void back() {
        for (Entity crystal : mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal && !e.isDead).sorted(Comparator.comparing(e -> mc.player.getDistance(e))).collect(Collectors.toList())) {
            if (crystal instanceof EntityEnderCrystal) {
                mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));

            }
        }
    }

    public static boolean isInsideBlock() {
      Vec3d vcd =   mc.player.getEntityBoundingBox().offset(0, -1, 0).getCenter();
      if(mc.world.getCollisionBoxes(mc.player,  new AxisAlignedBB(vcd.x+0.001,vcd.y+0.0005,vcd.z+0.001,vcd.x-0.001,vcd.y-0.0005,vcd.z-0.005)).isEmpty())return true;
         return getBurrowValves(new BlockPos(mc.player));



    }

    public static final List<Block> emptyBlocks = Arrays.asList(Blocks.AIR, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.VINE, Blocks.SNOW_LAYER, Blocks.TALLGRASS, Blocks.FIRE);
    public static boolean getBurrowValves(BlockPos blockPos) {
        if (emptyBlocks.contains(mc.world.getBlockState(blockPos).getBlock()) && mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox()).isEmpty() && mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, 1, 0)).isEmpty()) {
            return false;
        } else if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox()).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0, 1, 0)).isEmpty()) {
            if (emptyBlocks.contains(mc.world.getBlockState(blockPos).getBlock()) && emptyBlocks.contains(mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock())) return false;
        }
        return true;
    }
    public static void setPositionPacket(double x, double y, double z, boolean onGround, boolean setPos) {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, onGround));
        if (setPos) {
            mc.player.setPosition(x, y, z);
        }
    }

    public static BlockPos getPlayerPos() {
        return Math.abs(mc.player.motionY) > 0.1
                ? new BlockPos(mc.player)
                : PositionUtil.getPosition(mc.player);
    }

    public static double getY(Entity entity, double min, double max, boolean add) {
        if (min > max && add || max > min && !add) {
            return Double.NaN;
        }

        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;

        boolean air = false;
        double lastOff = 0.0;
        BlockPos last = null;
        for (double off = min;
             add ? off < max : off > max;
            //noinspection ConstantConditions ??? intellij drunk
             off = (add ? ++off : --off)) {
            BlockPos pos = new BlockPos(x, y - off, z);
            if (pos.getY() < 0) {
                continue;
            }

            if (Math.abs(y) < 1) {
                air = false;
                last = pos;
                lastOff = y - off;
                continue;
            }

            IBlockState state = mc.world.getBlockState(pos);
            if (!state.getMaterial().blocksMovement()
                    || state.getBlock() == Blocks.AIR) {
                if (air) {
                    if (add) {
                        return pos.getY();
                    } else {
                        return last.getY();
                    }
                }

                air = true;
            } else {
                air = false;
            }

            last = pos;
            lastOff = y - off;
        }

        return Double.NaN;
    }

    public static double getY(Entity entity, int mode) {

        if (mode == 1) {
            double y = entity.posY - 256;
            return y;
        }
        double d = getY(entity, 1, 5, true);
        if (Double.isNaN(d)) {
            d = getY(entity, -2, -5, false);
            if (Double.isNaN(d)) {

                return getY(entity, 1);

            }
        }

        return d;
    }

    public static double applyScale(double value) {

        return Math.floor(value);
    }


    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList();
        if (mc.world != null && pos != null) {
            EnumFacing[] var3 = EnumFacing.values();
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                EnumFacing side = var3[var5];
                BlockPos neighbour = pos.offset(side);
                IBlockState blockState = mc.world.getBlockState(neighbour);
                if (blockState != null && blockState.getBlock().canCollideCheck(blockState, false) && !blockState.getMaterial().isReplaceable()) {
                    facings.add(side);
                }
            }

            return facings;
        } else {
            return facings;
        }
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float) (vec.x - (double) pos.getX());
            float f1 = (float) (vec.y - (double) pos.getY());
            float f2 = (float) (vec.z - (double) pos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
        }

        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
    }

    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = null;
        Iterator<EnumFacing> iterator = getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            side = iterator.next();
        }

        if (side == null) {
            return isSneaking;
        } else {
            BlockPos neighbour = pos.offset(side);
            EnumFacing opposite = side.getOpposite();
            Vec3d hitVec = (new Vec3d(neighbour)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
            Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
            if (!mc.player.isSneaking() && (dev.cuican.staypro.utils.block.BlockInteractionHelper.blackList.contains(neighbourBlock) || dev.cuican.staypro.utils.block.BlockInteractionHelper.shulkerList.contains(neighbourBlock))) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                mc.player.setSneaking(true);
                sneaking = true;
            }

            if (rotate) {
                BlockInteractionHelper.faceVectorPacketInstant(hitVec);
            }

            rightClickBlock(neighbour, hitVec, hand, opposite, true);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.rightClickDelayTimer = 4;
            return sneaking || isSneaking;
        }
    }
}
