package dev.cuican.staypro.utils.block;


import dev.cuican.staypro.mixin.accessor.AccessorMinecraft;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import dev.cuican.staypro.utils.look;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class BlockUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static List blackList;
    public static List shulkerList;
    public static List<Block> emptyBlocks;
    public static List<Block> rightclickableBlocks;

    public static List<BlockPos> getDisc(BlockPos pos, float r) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        int x = cx - (int) r;
        while ((float) x <= (float) cx + r) {
            int z = cz - (int) r;
            while ((float) z <= (float) cz + r) {
                double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z);
                if (dist < (double) (r * r)) {
                    BlockPos position = new BlockPos(x, cy, z);
                    circleblocks.add(position);
                }
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }
    public static boolean isAir(BlockPos pos)
    {
        Block block = mc.world.getBlockState(pos).getBlock();
        return block instanceof BlockAir;
    }
    public static double getDirection2D(double dx, double dy)
    {
        double d;
        if(dy==0) {
            if(dx>0) {
                d = 90;
            }else {
                d = -90;
            }
        }else {
            d = Math.atan(dx/dy) * 57.2957796;
            if(dy<0) {
                if(dx>0) {
                    d+=180;
                }else {
                    if(dx<0) {
                        d -= 180;
                    }else {
                        d = 180;
                    }
                }
            }
        }
        return d;
    }
    protected final Vec3d getVectorForRotation(double pitch, double yaw)
    {
        float f = MathHelper.cos((float) (-yaw * 0.017453292F - (float)Math.PI));
        float f1 = MathHelper.sin((float) (-yaw * 0.017453292F - (float)Math.PI));
        float f2 = -MathHelper.cos((float) (-pitch * 0.017453292F));
        float f3 = MathHelper.sin((float) (-pitch * 0.017453292F));
        return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
    }
    public boolean doPlace(boolean swing) {
        double dx=((pos.getX()+0.5-mc.player.posX) - ((double)f.getDirectionVec().getX())/2 );
        double dy=((pos.getY()+0.5-mc.player.posY) - ((double)f.getDirectionVec().getY())/2 )-mc.player.getEyeHeight();
        double dz=((pos.getZ()+0.5-mc.player.posZ) - ((double)f.getDirectionVec().getZ())/2 );

        double x=getDirection2D(dz, dx);
        double y=getDirection2D(dy, Math.sqrt(dx*dx+dz*dz));

        Vec3d vec = getVectorForRotation(-y, x-90);

        this.roty=-y;
        this.rotx=x-90;

        EnumActionResult enumactionresult = mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(f, -1), f, vec, EnumHand.MAIN_HAND);
        if (enumactionresult == EnumActionResult.SUCCESS)
        {
            if (swing)
                mc.player.swingArm(EnumHand.MAIN_HAND);

            return true;
        }
        return false;
    }

    public static boolean doPlace(BlockUtil event, boolean swing) {
        if (event == null)
            return false;

        return event.doPlace(swing);
    }

    public BlockUtil(BlockPos pos, int a, EnumFacing f, double dist) {
        this.pos=pos;
        this.a=a;
        this.f=f;
        this.dist=dist;
    }

    public BlockPos pos;
    public int a;
    public EnumFacing f;
    public double dist;
    public double rotx, roty;
    public static BlockUtil isPlaceable(BlockPos pos, double dist, boolean Collide) {
        BlockUtil event = new BlockUtil(pos, 0, null, dist);

        if(!isAir(pos))
            return null;

        AxisAlignedBB axisalignedbb = Block.FULL_BLOCK_AABB;

        if(!isAir(pos)) {
            if (mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                Block block = mc.world.getBlockState(pos.offset(EnumFacing.UP)).getBlock();
                if (block instanceof BlockLiquid) {
                    event.f = EnumFacing.DOWN;
                    event.pos.offset(EnumFacing.UP);
                }else {
                    event.f = EnumFacing.UP;
                    event.pos.offset(EnumFacing.DOWN);
                }
                return event;
            }
        }

        for(EnumFacing f : EnumFacing.values())
        {
            if(!isAir(new BlockPos(pos.getX()-f.getDirectionVec().getX(), pos.getY()-f.getDirectionVec().getY(), pos.getZ()-f.getDirectionVec().getZ())))
            {
                event.f=f;


                if (Collide && axisalignedbb != Block.NULL_AABB && !mc.world.checkNoEntityCollision(axisalignedbb.offset(pos), (Entity)null))
                {
                    return null;
                }

                return event;
            }
        }
        if(isRePlaceable(pos))
        {
            event.f=EnumFacing.UP;
            event.pos.offset(EnumFacing.UP);
            pos.offset(EnumFacing.DOWN);

            if (Collide && axisalignedbb != Block.NULL_AABB && !mc.world.checkNoEntityCollision(axisalignedbb.offset(pos), (Entity)null))
            {
                return null;
            }

            return event;
        }
        return null;
    }
    public static boolean isRePlaceable(BlockPos pos)
    {
        Block block = mc.world.getBlockState(pos).getBlock();
        return block.isReplaceable(mc.world, pos) && !(block instanceof BlockAir);
    }
    public static boolean canBlockBeSeen(double x, double y, double z) {
        if (BlockUtil.mc.world.rayTraceBlocks(new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double)BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ), new Vec3d(x, y + 1.7, z), false, true, false) != null) {
            return false;
        }
        return true;
    }
    static {
        emptyBlocks = Arrays.asList(Blocks.AIR, Blocks.FLOWING_LAVA, Blocks.LAVA, Blocks.FLOWING_WATER, Blocks.WATER, Blocks.VINE, Blocks.SNOW_LAYER, Blocks.TALLGRASS, Blocks.FIRE);
        rightclickableBlocks = Arrays.asList(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.ANVIL, Blocks.WOODEN_BUTTON, Blocks.STONE_BUTTON, Blocks.UNPOWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.POWERED_COMPARATOR, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.BREWING_STAND, Blocks.DISPENSER, Blocks.DROPPER, Blocks.LEVER, Blocks.NOTEBLOCK, Blocks.JUKEBOX, Blocks.BEACON, Blocks.BED, Blocks.FURNACE, Blocks.OAK_DOOR, Blocks.SPRUCE_DOOR, Blocks.BIRCH_DOOR, Blocks.JUNGLE_DOOR, Blocks.ACACIA_DOOR, Blocks.DARK_OAK_DOOR, Blocks.CAKE, Blocks.ENCHANTING_TABLE, Blocks.DRAGON_EGG, Blocks.HOPPER, Blocks.REPEATING_COMMAND_BLOCK, Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.CRAFTING_TABLE);
    }

    static {
        blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER);
        shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    }

    public static Vec3d[] getHelpingBlocks(final Vec3d vec3d) {
        return new Vec3d[]{new Vec3d(vec3d.x, vec3d.y - 1.0, vec3d.z), new Vec3d((vec3d.x != 0.0) ? (vec3d.x * 2.0) : vec3d.x, vec3d.y, (vec3d.x != 0.0) ? vec3d.z : (vec3d.z * 2.0)), new Vec3d((vec3d.x == 0.0) ? (vec3d.x + 1.0) : vec3d.x, vec3d.y, (vec3d.x == 0.0) ? vec3d.z : (vec3d.z + 1.0)), new Vec3d((vec3d.x == 0.0) ? (vec3d.x - 1.0) : vec3d.x, vec3d.y, (vec3d.x == 0.0) ? vec3d.z : (vec3d.z - 1.0)), new Vec3d(vec3d.x, vec3d.y + 1.0, vec3d.z)};
    }

    public static EnumFacing getRayTraceFacing(final BlockPos pos) {
        final RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getX() - 0.5, pos.getX() + 0.5));
        if (result == null || result.sideHit == null) {
            return EnumFacing.UP;
        }
        return result.sideHit;
    }

    public static BlockPos[] toBlockPos(Vec3d[] vec3ds) {
        BlockPos[] list = new BlockPos[vec3ds.length];
        for (int i = 0; i < vec3ds.length; ++i) {
            list[i] = new BlockPos(vec3ds[i]);
        }
        return list;
    }

    public static EnumFacing getFacing(BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            RayTraceResult rayTraceResult = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) pos.getX() + 0.5 + (double) facing.getDirectionVec().getX() * 1.0 / 2.0, (double) pos.getY() + 0.5 + (double) facing.getDirectionVec().getY() * 1.0 / 2.0, (double) pos.getZ() + 0.5 + (double) facing.getDirectionVec().getZ() * 1.0 / 2.0), false, true, false);
            if (rayTraceResult != null && (rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK || !rayTraceResult.getBlockPos().equals(pos)))
                continue;
            return facing;
        }
        if ((double) pos.getY() > mc.player.posY + (double) mc.player.getEyeHeight()) {
            return EnumFacing.DOWN;
        }
        return EnumFacing.UP;
    }

    public static boolean isElseHole(final BlockPos blockPos) {
        for (final BlockPos pos : getTouchingBlocks(blockPos)) {
            IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || !touchingState.isFullBlock()) {
                return false;
            }
        }
        return true;
    }

    public static BlockPos[] getTouchingBlocks(final BlockPos blockPos) {
        return new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
    }

    public static List<EnumFacing> getPossibleSides(final BlockPos pos) {
        List<EnumFacing> facings = new ArrayList<EnumFacing>();
        if (mc.world == null || pos == null) {
            return facings;
        }
        for (final EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (blockState != null && blockState.getBlock().canCollideCheck(blockState, false) && !blockState.getMaterial().isReplaceable()) {
                facings.add(side);
            }
        }
        return facings;
    }

    public static EnumFacing getFirstFacing(final BlockPos pos) {
        Iterator<EnumFacing> iterator = getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            EnumFacing facing = iterator.next();
            return facing;
        }
        return null;
    }

    public static boolean placeBlock(final BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
        if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
            mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            faceVectorPacketInstant(hitVec);
        }
        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        ((AccessorMinecraft) mc).setRightClickDelayTimer(0);
        return sneaking || isSneaking;
    }

    public static boolean placeBlockSmartRotate(final BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = getFirstFacing(pos);
        ChatUtil.sendNoSpamMessage(side.toString());
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
        if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
            sneaking = true;
        }
        if (rotate) {
            look.lookAt(new Vec3d(hitVec.x, hitVec.y, hitVec.y));
        }
        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        ((AccessorMinecraft) mc).setRightClickDelayTimer(0) ;
        return sneaking || isSneaking;
    }

    public static int isPositionPlaceable(final BlockPos pos, boolean rayTrace) {
        return isPositionPlaceable(pos, rayTrace, true);
    }

    public static int isPositionPlaceable(final BlockPos pos, boolean rayTrace, boolean entityCheck) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
            return 0;
        }
        if (!rayTracePlaceCheck(pos, rayTrace, 0.0f)) {
            return -1;
        }
        if (entityCheck) {
            for (final Object entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (!(entity instanceof EntityItem)) {
                    if (entity instanceof EntityXPOrb) {
                        continue;
                    }
                    return 1;
                }
            }
        }
        for (final EnumFacing side : getPossibleSides(pos)) {
            if (!canBeClicked(pos.offset(side))) {
                continue;
            }
            return 3;
        }
        return 2;
    }

    public static Vec3d[] convertVec3ds(final Vec3d vec3d, Vec3d[] input) {
        Vec3d[] output = new Vec3d[input.length];
        for (int i = 0; i < input.length; ++i) {
            output[i] = vec3d.add(input[i]);
        }
        return output;
    }

    public static Vec3d[] convertVec3ds(final EntityPlayer entity, Vec3d[] input) {
        return convertVec3ds(entity.getPositionVector(), input);
    }

    public static void rightClickBlock(final BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float) (vec.x - pos.getX());
            float f2 = (float) (vec.y - pos.getY());
            float f3 = (float) (vec.z - pos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f2, f3));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
        }
        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        ((AccessorMinecraft) mc).setRightClickDelayTimer(0);
    }


    public static boolean canBreak(final BlockPos pos) {
        IBlockState blockState = mc.world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (mc.world.getBlockState(pos).getBlock() == Blocks.AIR) {
            return false;
        }
        if (mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK) {
            return false;
        }
        if (mc.world.getBlockState(pos).getBlock() == Blocks.END_PORTAL_FRAME) {
            return false;
        }
        if (mc.world.getBlockState(pos).getBlock() == Blocks.END_PORTAL) {
            return false;
        }
        return block.getBlockHardness(blockState, mc.world, pos) != -1.0f;
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
                continue;
            }
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (!blockState.getMaterial().isReplaceable()) {
                return side;
            }
        }
        return null;
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = getNeededRotations2(vec);
        mc.player.connection.sendPacket(new Rotation(rotations[0], rotations[1], mc.player.onGround));
    }
    public static void faceVectorPacketInstant(Vec3d vec, Boolean roundAngles) {
        float[] rotations = getNeededRotations2(vec);

        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], roundAngles ? MathHelper.normalizeAngle((int) rotations[1], 360) : rotations[1], mc.player.onGround));
    }

    private static float[] getNeededRotations2(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
        return new float[]{
                mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
                mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)
        };
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static int getDirection4D() {
        return MathHelper.floor(mc.player.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
    }

    public static String getDirection4D(final boolean northRed) {
        int dirnumber = getDirection4D();
        if (dirnumber == 0) {
            return "South (+Z)";
        }
        if (dirnumber == 1) {
            return "West (-X)";
        }
        if (dirnumber == 2) {
            return (northRed ? "\u00c2�c" : "") + "North (-Z)";
        }
        if (dirnumber == 3) {
            return "East (+X)";
        }
        return "Loading...";
    }

    public static Boolean isPosInFov(final BlockPos pos) {
        int dirnumber = getDirection4D();
        if (dirnumber == 0 && pos.getZ() - mc.player.getPositionVector().z < 0.0) {
            return false;
        }
        if (dirnumber == 1 && pos.getX() - mc.player.getPositionVector().x > 0.0) {
            return false;
        }
        if (dirnumber == 2 && pos.getZ() - mc.player.getPositionVector().z > 0.0) {
            return false;
        }
        return dirnumber != 3 || pos.getX() - mc.player.getPositionVector().x >= 0.0;
    }



    public static boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            return ((mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || mc.world
                    .getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && mc.world
                    .getBlockState(boost).getBlock() == Blocks.AIR && mc.world
                    .getBlockState(boost2).getBlock() == Blocks.AIR && mc.world
                    .getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world
                    .getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty());
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck, boolean oneDot15) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        try {
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN)
                return false;
            if ((mc.world.getBlockState(boost).getBlock() != Blocks.AIR || mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) && !oneDot15)
                return false;
            if (specialEntityCheck) {
                for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if (!(entity instanceof net.minecraft.entity.item.EntityEnderCrystal))
                        return false;
                }
                if (!oneDot15)
                    for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                        if (!(entity instanceof net.minecraft.entity.item.EntityEnderCrystal))
                            return false;
                    }
            } else {
                return (mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && (oneDot15 || mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty()));
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; ) {
                int y = sphere ? (cy - (int) r) : cy;
                for (; ; z++) {
                    if (y < (sphere ? (cy + r) : (cy + h))) {
                        double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0));
                        if (dist < (r * r) && (!hollow || dist >= ((r - 1.0F) * (r - 1.0F)))) {
                            BlockPos l = new BlockPos(x, y + plus_y, z);
                            circleblocks.add(l);
                        }
                        y++;
                        continue;
                    }
                }
            }
        }
        return circleblocks;
    }

    public static List<BlockPos> getSphere(float radius) {
        return getSphere(getPosition(), radius);
    }

    public static BlockPos getPosition() {
        return getPosition(mc.player);
    }

    public static BlockPos getPosition(Entity entity) {
        return new BlockPos(entity.posX, entity.posY, entity.posZ);
    }

    public static List<BlockPos> getSphere(BlockPos pos, float radius) {
        List<BlockPos> sphere = new ArrayList<>();

        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();

        for (int x = posX - (int) radius; x <= posX + radius; x++) {
            for (int z = posZ - (int) radius; z <= posZ + radius; z++) {
                for (int y = posY - (int) radius; y < posY + radius; y++) {
                    double dist = (posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y);
                    if (dist < radius * radius) {
                        BlockPos position = new BlockPos(x, y, z);
                        sphere.add(position);
                    }
                }
            }
        }

        return sphere;
    }

    public static void placeCrystalOnBlock(BlockPos pos, EnumHand hand, boolean swing, boolean exactHand) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() - 0.5, (double) pos.getZ() + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.5f, 0.5f, 0.5f));
        if (swing) {
            mc.player.connection.sendPacket(new CPacketAnimation(exactHand ? hand : EnumHand.MAIN_HAND));
        }
    }

    public static void placeCrystalOnBlock(final BlockPos pos, EnumHand hand) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5));
        EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
    }

    public static boolean rayTracePlaceCheck(final BlockPos pos, boolean shouldCheck, float height) {
        return !shouldCheck || mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX(), pos.getY() + height, pos.getZ()), false, true, false) == null;
    }

    public static boolean rayTracePlaceCheck(final BlockPos pos, boolean shouldCheck) {
        return rayTracePlaceCheck(pos, shouldCheck, 1.0f);
    }


    public static void openBlock(BlockPos pos) {
        EnumFacing[] facings = EnumFacing.values();

        for (EnumFacing f : facings) {
            Block neighborBlock = mc.world.getBlockState(pos.offset(f)).getBlock();

            if (emptyBlocks.contains(neighborBlock)) {
                mc.playerController.processRightClickBlock(mc.player, mc.world, pos, f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);

                return;
            }
        }
    }

    public static boolean placeBlockTrap(BlockPos pos , int slot , boolean rotate, boolean packet , boolean swing) {
        int oldSlot = mc.player.inventory.currentItem;
        if (isBlockEmpty(pos)) {
            EnumFacing[] facings = EnumFacing.values();
            InventoryUtil.switchToHotbarSlot(slot , false);
            for (EnumFacing f : facings) {
                Block neighborBlock = mc.world.getBlockState(pos.offset(f)).getBlock();
                Vec3d vec = new Vec3d(pos.getX() + 0.5D + (double) f.getXOffset() * 0.5D, pos.getY() + 0.5D + (double) f.getYOffset() * 0.5D, pos.getZ() + 0.5D + (double) f.getZOffset() * 0.5D);

                if (!emptyBlocks.contains(neighborBlock) && mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(vec) <= 4.25D) {
                    float[] rot = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};

                    if (rotate) {
                        rotatePacket(vec.x, vec.y, vec.z);
                    }

                    if (rightclickableBlocks.contains(neighborBlock)) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
                    }
                    placeBlock(pos, EnumHand.MAIN_HAND, rotate, packet, mc.player.isSneaking());
                    //mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(f), f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
                    /*
                    if(!mc.player.isCreative() && mc.getConnection() != null) {
                        if (ModuleManager.getModuleByName("InstantBreak").isEnabled()) {
                            if (Instant.getInstance().currentPos == null) {
                                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
                            }
                        }
                    }

                     */
                    if (rightclickableBlocks.contains(neighborBlock)) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
                    }

                    if (rotate) {
                        mc.player.connection.sendPacket(new Rotation(rot[0], rot[1], mc.player.onGround));
                    }
                    if (swing) {
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                    }
                    if (oldSlot != -1) {
                        InventoryUtil.switchToHotbarSlot(oldSlot, false);
                    }
                    return true;
                }
            }

        }

        return false;
    }

    public static boolean placeBlock(BlockPos pos, int slot, boolean rotate, boolean rotateBack) {
        if (isBlockEmpty(pos)) {
            int old_slot = -1;
            if (slot != mc.player.inventory.currentItem) {
                old_slot = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = slot;
            }

            EnumFacing[] facings = EnumFacing.values();

            for (EnumFacing f : facings) {
                Block neighborBlock = mc.world.getBlockState(pos.offset(f)).getBlock();
                Vec3d vec = new Vec3d(pos.getX() + 0.5D + (double) f.getXOffset() * 0.5D, pos.getY() + 0.5D + (double) f.getYOffset() * 0.5D, pos.getZ() + 0.5D + (double) f.getZOffset() * 0.5D);

                if (!emptyBlocks.contains(neighborBlock) && mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(vec) <= 4.25D) {
                    float[] rot = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};

                    if (rotate) {
                        rotatePacket(vec.x, vec.y, vec.z);
                    }

                    if (rightclickableBlocks.contains(neighborBlock)) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
                    }

                    mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(f), f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
                    if (rightclickableBlocks.contains(neighborBlock)) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
                    }

                    if (rotateBack) {
                        mc.player.connection.sendPacket(new Rotation(rot[0], rot[1], mc.player.onGround));
                    }
                    mc.player.swingArm(EnumHand.OFF_HAND);
                    if (old_slot != -1) {
                        mc.player.inventory.currentItem = old_slot;
                    }

                    return true;
                }
            }

        }

        return false;
    }
    public static boolean placeBlock(BlockPos pos, boolean rotate, boolean rotateBack) {
        if (isBlockEmpty(pos)) {

            EnumFacing[] facings = EnumFacing.values();

            for (EnumFacing f : facings) {
                Block neighborBlock = mc.world.getBlockState(pos.offset(f)).getBlock();
                Vec3d vec = new Vec3d(pos.getX() + 0.5D + (double) f.getXOffset() * 0.5D, pos.getY() + 0.5D + (double) f.getYOffset() * 0.5D, pos.getZ() + 0.5D + (double) f.getZOffset() * 0.5D);
                if (!emptyBlocks.contains(neighborBlock) && mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(vec) <= 4.25D) {
                    float[] rot = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};

                    if (rotate) {
                        rotatePacket(vec.x, vec.y, vec.z);
                    }

                    if (rightclickableBlocks.contains(neighborBlock)) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.START_SNEAKING));
                    }

                    mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(f), f.getOpposite(), new Vec3d(pos), EnumHand.MAIN_HAND);
                    if (rightclickableBlocks.contains(neighborBlock)) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, Action.STOP_SNEAKING));
                    }

                    if (rotateBack) {
                        mc.player.connection.sendPacket(new Rotation(rot[0], rot[1], mc.player.onGround));
                    }
                    mc.player.swingArm(EnumHand.OFF_HAND);
                    return true;
                }
            }

        }

        return false;
    }

    public static boolean isBlockEmpty(BlockPos pos) {
        try {
            if (emptyBlocks.contains(mc.world.getBlockState(pos).getBlock())) {
                AxisAlignedBB box = new AxisAlignedBB(pos);
                Iterator entityIter = mc.world.loadedEntityList.iterator();

                Entity e;

                do {
                    if (!entityIter.hasNext()) {
                        return true;
                    }

                    e = (Entity) entityIter.next();
                } while (!(e instanceof EntityLivingBase) || !box.intersects(e.getEntityBoundingBox()));

            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean canPlaceBlock(BlockPos pos) {
        if (isBlockEmpty(pos)) {
            EnumFacing[] facings = EnumFacing.values();

            for (EnumFacing f : facings) {
                if (!emptyBlocks.contains(mc.world.getBlockState(pos.offset(f)).getBlock()) && mc.player.getPositionEyes(mc.getRenderPartialTicks()).distanceTo(new Vec3d(pos.getX() + 0.5D + (double) f.getXOffset() * 0.5D, pos.getY() + 0.5D + (double) f.getYOffset() * 0.5D, pos.getZ() + 0.5D + (double) f.getZOffset() * 0.5D)) <= 4.25D) {
                    return true;
                }
            }

        }
        return false;
    }

    public static void rotatePacket(double x, double y, double z) {
        double diffX = x - mc.player.posX;
        double diffY = y - (mc.player.posY + (double) mc.player.getEyeHeight());
        double diffZ = z - mc.player.posZ;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        mc.player.connection.sendPacket(new Rotation(yaw, pitch, mc.player.onGround));
    }

    public static Vec3d getHitVec(BlockPos pos, EnumFacing facing) {
        Vec3i vec = facing.getDirectionVec();
        return new Vec3d(vec.getX() * 0.5 + 0.5 + pos.getX(), vec.getY() * 0.5 + 0.5 + pos.getY(), vec.getZ() * 0.5 + 0.5 + pos.getZ());
    }

    public static ValidResult valid(BlockPos pos) {
        // There are no entities to block placement,
        if (!mc.world.checkNoEntityCollision(new AxisAlignedBB(pos)))
            return ValidResult.NoEntityCollision;

        if (!checkForNeighbours(pos))
            return ValidResult.NoNeighbors;

        IBlockState l_State = mc.world.getBlockState(pos);

        if (l_State.getBlock() == Blocks.AIR) {
            final BlockPos[] l_Blocks =
                    {pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()};

            for (BlockPos l_Pos : l_Blocks) {
                IBlockState l_State2 = mc.world.getBlockState(l_Pos);

                if (l_State2.getBlock() == Blocks.AIR)
                    continue;

                for (final EnumFacing side : EnumFacing.values()) {
                    final BlockPos neighbor = pos.offset(side);

                    if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false)) {
                        return ValidResult.Ok;
                    }
                }
            }

            return ValidResult.NoNeighbors;
        }

        return ValidResult.AlreadyBlockThere;
    }

    public static boolean checkForNeighbours(final BlockPos blockPos) {
        if (!hasNeighbour(blockPos)) {
            for (final EnumFacing side : EnumFacing.values()) {
                final BlockPos neighbour = blockPos.offset(side);
                if (hasNeighbour(neighbour)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private static boolean hasNeighbour(final BlockPos blockPos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = blockPos.offset(side);
            if (!mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) {
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

}
