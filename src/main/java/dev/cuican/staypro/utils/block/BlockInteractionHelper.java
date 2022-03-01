package dev.cuican.staypro.utils.block;


import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.mixin.accessor.AccessorMinecraft;
import dev.cuican.staypro.utils.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hub on 15 June 2019
 * Last Updated 12 January 2019 by hub
 */
public class BlockInteractionHelper {
    public static final List<Block> blackList = Arrays.asList(
            Blocks.ENDER_CHEST,
            Blocks.CHEST,
            Blocks.TRAPPED_CHEST,
            Blocks.CRAFTING_TABLE,
            Blocks.ANVIL,
            Blocks.BREWING_STAND,
            Blocks.HOPPER,
            Blocks.DROPPER,
            Blocks.DISPENSER,
            Blocks.TRAPDOOR,
            Blocks.ENCHANTING_TABLE
    );
    public static final List<Block> shulkerList = Arrays.asList(
            Blocks.WHITE_SHULKER_BOX,
            Blocks.ORANGE_SHULKER_BOX,
            Blocks.MAGENTA_SHULKER_BOX,
            Blocks.LIGHT_BLUE_SHULKER_BOX,
            Blocks.YELLOW_SHULKER_BOX,
            Blocks.LIME_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX,
            Blocks.GRAY_SHULKER_BOX,
            Blocks.SILVER_SHULKER_BOX,
            Blocks.CYAN_SHULKER_BOX,
            Blocks.PURPLE_SHULKER_BOX,
            Blocks.BLUE_SHULKER_BOX,
            Blocks.BROWN_SHULKER_BOX,
            Blocks.GREEN_SHULKER_BOX,
            Blocks.RED_SHULKER_BOX,
            Blocks.BLACK_SHULKER_BOX
    );
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static ConcurrentHashMap<AxisAlignedBB, Color> holes;

    public static EnumFacing getPlaceableSide(final BlockPos pos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = pos.offset(side);
            if (BlockInteractionHelper.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(BlockInteractionHelper.mc.world.getBlockState(neighbour), false)) {
                final IBlockState blockState = BlockInteractionHelper.mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable()) {
                    return side;
                }
            }
        }
        return null;
    }

    public static List<BlockPos> getLegVec(Vec3d add) {
        List<BlockPos> circleblocks = new ArrayList<>();
        BlockPos uwu = new BlockPos(add.x , add.y , add.z);
        circleblocks.add(uwu);
        return circleblocks;
    }

    public static List<BlockPos> getBrokenHole(BlockPos doubleTargetPos) {
        List<BlockPos> circleblocks = new ArrayList<>();
        final BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            final IBlockState touchingState = mc.world.getBlockState(blockPos.offset(facing));
            if (touchingState.getBlock() == Blocks.AIR) {
                doubleTargetPos = blockPos.offset(facing);
            }
            circleblocks.add(doubleTargetPos);
        }
        return circleblocks;
    }

    public static ValidResult valid(final BlockPos pos) {
        if (!BlockInteractionHelper.mc.world.checkNoEntityCollision(new AxisAlignedBB(pos))) {
            return ValidResult.NoEntityCollision;
        }
        if (BlockInteractionHelper.mc.world.getBlockState(pos.down()).getBlock() == Blocks.WATER && ModuleManager.getModuleByName("LiquidInteract").isEnabled()) {
            return ValidResult.Ok;
        }
        if (!checkForNeighbours(pos)) {
            return ValidResult.NoNeighbors;
        }
        final IBlockState l_State = BlockInteractionHelper.mc.world.getBlockState(pos);
        if (l_State.getBlock() == Blocks.AIR) {
            final BlockPos[] array;
            final BlockPos[] l_Blocks = array = new BlockPos[]{pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()};
            for (final BlockPos l_Pos : array) {
                final IBlockState l_State2 = BlockInteractionHelper.mc.world.getBlockState(l_Pos);
                if (l_State2.getBlock() != Blocks.AIR) {
                    for (final EnumFacing side : EnumFacing.values()) {
                        final BlockPos neighbor = pos.offset(side);
                        final boolean l_IsWater = BlockInteractionHelper.mc.world.getBlockState(neighbor).getBlock() == Blocks.WATER;
                        if (BlockInteractionHelper.mc.world.getBlockState(neighbor).getBlock().canCollideCheck(BlockInteractionHelper.mc.world.getBlockState(neighbor), false) || (l_IsWater && ModuleManager.getModuleByName("LiquidInteract").isEnabled())) {
                            return ValidResult.Ok;
                        }
                    }
                }
            }
            return ValidResult.NoNeighbors;
        }
        return ValidResult.AlreadyBlockThere;
    }

    public static PlaceResult place(final BlockPos pos, final float p_Distance, final boolean p_Rotate, final boolean p_UseSlabRule) {
        return place(pos, p_Distance, p_Rotate, p_UseSlabRule, false);
    }

    public static PlaceResult place(final BlockPos pos, final float p_Distance, final boolean p_Rotate, final boolean p_UseSlabRule, final boolean packetSwing) {
        final IBlockState l_State = BlockInteractionHelper.mc.world.getBlockState(pos);
        final boolean l_Replaceable = l_State.getMaterial().isReplaceable();
        final boolean l_IsSlabAtBlock = l_State.getBlock() instanceof BlockSlab;
        if (!l_Replaceable && !l_IsSlabAtBlock) {
            return PlaceResult.NotReplaceable;
        }
        if (!checkForNeighbours(pos)) {
            return PlaceResult.Neighbors;
        }
        if (p_UseSlabRule && l_IsSlabAtBlock && !l_State.isFullCube()) {
            return PlaceResult.CantPlace;
        }
        final Vec3d eyesPos = new Vec3d(BlockInteractionHelper.mc.player.posX, BlockInteractionHelper.mc.player.posY + BlockInteractionHelper.mc.player.getEyeHeight(), BlockInteractionHelper.mc.player.posZ);
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            final boolean l_IsWater = BlockInteractionHelper.mc.world.getBlockState(neighbor).getBlock() == Blocks.WATER;
            if (BlockInteractionHelper.mc.world.getBlockState(neighbor).getBlock().canCollideCheck(BlockInteractionHelper.mc.world.getBlockState(neighbor), false) || (l_IsWater && ModuleManager.getModuleByName("LiquidInteract").isEnabled())) {
                final Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                if (eyesPos.distanceTo(hitVec) <= p_Distance) {
                    final Block neighborPos = BlockInteractionHelper.mc.world.getBlockState(neighbor).getBlock();
                    final boolean activated = neighborPos.onBlockActivated(BlockInteractionHelper.mc.world, pos, BlockInteractionHelper.mc.world.getBlockState(pos), BlockInteractionHelper.mc.player, EnumHand.MAIN_HAND, side, 0.0f, 0.0f, 0.0f);
                    if (BlockInteractionHelper.blackList.contains(neighborPos) || BlockInteractionHelper.shulkerList.contains(neighborPos) || activated) {
                        BlockInteractionHelper.mc.player.connection.sendPacket(new CPacketEntityAction(BlockInteractionHelper.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    }
                    if (p_Rotate) {
                        faceVectorPacketInstant(hitVec);
                    }
                    final EnumActionResult l_Result2 = BlockInteractionHelper.mc.playerController.processRightClickBlock(BlockInteractionHelper.mc.player, BlockInteractionHelper.mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                    if (l_Result2 != EnumActionResult.FAIL) {
                        if (packetSwing) {
                            BlockInteractionHelper.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                        } else {
                            BlockInteractionHelper.mc.player.swingArm(EnumHand.MAIN_HAND);
                        }
                        if (activated) {
                            BlockInteractionHelper.mc.player.connection.sendPacket(new CPacketEntityAction(BlockInteractionHelper.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                        }
                        return PlaceResult.Placed;
                    }
                }
            }
        }
        return PlaceResult.CantPlace;
    }

    public static double blockDistance(final double blockposx, final double blockposy, final double blockposz, final double blockposx1, final double blockposy1, final double blockposz1) {
        final double deltaX = blockposx1 - blockposx;
        final double deltaY = blockposy1 - blockposy;
        final double deltaZ = blockposz1 - blockposz;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

    public static double blockDistance(final double n, final double n2, final double n3, final Entity entity) {
        final double n4 = entity.posX - n;
        final double n5 = entity.posY - n2;
        final double n6 = entity.posZ - n3;
        return Math.sqrt(n4 * n4 + n5 * n5 + n6 * n6);
    }

    public static double blockDistance2d(final double n, final double n2, final Entity entity) {
        final double n3 = entity.posX - n;
        final double n4 = entity.posZ - n2;
        return Math.sqrt(n3 * n3 + n4 * n4);
    }

    public static double[] directionSpeed(final double speed) {
        final Minecraft mc = Minecraft.getMinecraft();
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static void placeBlockScaffold(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(Wrapper.getPlayer().posX,
                Wrapper.getPlayer().posY + Wrapper.getPlayer().getEyeHeight(),
                Wrapper.getPlayer().posZ);

        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();

            // check if neighbor can be right clicked
            if (!canBeClicked(neighbor)) {
                continue;
            }

            Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5)
                    .add(new Vec3d(side2.getDirectionVec()).scale(0.5));

            // check if hitVec is within range (4.25 blocks)
            if (eyesPos.squareDistanceTo(hitVec) > 18.0625) {
                continue;
            }

            // place block
            faceVectorPacketInstant(hitVec);
            processRightClickBlock(neighbor, side2, hitVec);
            Wrapper.getPlayer().swingArm(EnumHand.MAIN_HAND);
            ((AccessorMinecraft) mc).setRightClickDelayTimer(0);

            return;
        }

    }

    public static float[] calcAngle(final Vec3d from, final Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0;
        final double difZ = to.z - from.z;
        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

    public static List<BlockPos> getSphere(final BlockPos loc, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final List<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = loc.getX();
        final int cy = loc.getY();
        final int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; ++x) {
            for (int z = cz - (int) r; z <= cz + r; ++z) {
                for (int y = sphere ? (cy - (int) r) : cy; y < (sphere ? (cy + r) : ((float) (cy + h))); ++y) {
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        final BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static List<BlockPos> getCircle(final BlockPos loc, final int y, final float r, final boolean hollow) {
        final List<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = loc.getX();
        final int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; ++x) {
            for (int z = cz - (int) r; z <= cz + r; ++z) {
                final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z);
                if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                    final BlockPos l = new BlockPos(x, y, z);
                    circleblocks.add(l);
                }
            }
        }
        return circleblocks;
    }

    private static float[] getLegitRotations(BlockPos vec) {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.getX() - eyesPos.x;
        double diffY = vec.getY() - eyesPos.y;
        double diffZ = vec.getZ() - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{
                Wrapper.getPlayer().rotationYaw
                        + MathHelper.wrapDegrees(yaw - Wrapper.getPlayer().rotationYaw),
                Wrapper.getPlayer().rotationPitch + MathHelper
                        .wrapDegrees(pitch - Wrapper.getPlayer().rotationPitch)};
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{
                Wrapper.getPlayer().rotationYaw
                        + MathHelper.wrapDegrees(yaw - Wrapper.getPlayer().rotationYaw),
                Wrapper.getPlayer().rotationPitch + MathHelper
                        .wrapDegrees(pitch - Wrapper.getPlayer().rotationPitch)};
    }

    private static Vec3d getEyesPos() {
        return new Vec3d(Wrapper.getPlayer().posX,
                Wrapper.getPlayer().posY + Wrapper.getPlayer().getEyeHeight(),
                Wrapper.getPlayer().posZ);
    }

    public static void faceBlockPosPacketInstant(BlockPos vec) {
        float[] rotations = getLegitRotations(vec);

        Wrapper.getPlayer().connection.sendPacket(new CPacketPlayer.Rotation(rotations[0],
                rotations[1], Wrapper.getPlayer().onGround));
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = getLegitRotations(vec);

        Wrapper.getPlayer().connection.sendPacket(new CPacketPlayer.Rotation(rotations[0],
                rotations[1], Wrapper.getPlayer().onGround));
    }

    public static void processRightClickBlock(BlockPos pos, EnumFacing side,
                                              Vec3d hitVec) {
        getPlayerController().processRightClickBlock(Wrapper.getPlayer(),
                mc.world, pos, side, hitVec, EnumHand.MAIN_HAND);
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    private static PlayerControllerMP getPlayerController() {
        return Minecraft.getMinecraft().playerController;
    }

    private static IBlockState getState(BlockPos pos) {
        return Wrapper.getWorld().getBlockState(pos);
    }

    public static boolean checkForNeighbours(BlockPos blockPos) {
        // check if we don't have a block adjacent to blockpos
        if (!hasNeighbour(blockPos)) {
            // find air adjacent to blockpos that does have a block adjacent to it, let's fill this first as to form a bridge between the player and the original blockpos. necessary if the player is going diagonal.
            for (EnumFacing side : EnumFacing.values()) {
                BlockPos neighbour = blockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public static boolean hasNeighbour(BlockPos blockPos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = blockPos.offset(side);
            if (!Wrapper.getWorld().getBlockState(neighbour).getMaterial().isReplaceable()) {
                return true;
            }
        }
        return false;
    }

    public enum ValidResult {
        NoEntityCollision,
        AlreadyBlockThere,
        NoNeighbors,
        Ok,
    }

    public enum PlaceResult {
        NotReplaceable,
        Neighbors,
        CantPlace,
        Placed,
    }

}
