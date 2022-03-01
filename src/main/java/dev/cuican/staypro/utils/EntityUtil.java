package dev.cuican.staypro.utils;

import dev.cuican.staypro.client.FriendManager;
import dev.cuican.staypro.utils.block.BlockUtil;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityUtil {
    public static boolean isntValid(Entity entity, double range) {
        return (entity == null || isDead(entity) || (entity instanceof EntityPlayer && FriendManager.isFriend(entity.getName())));
    }



    public static BlockPos getRoundedBlockPos(Entity entity) {
        return new BlockPos(MathUtil.roundVec(entity.getPositionVector(), 0));
    }
    public static boolean isAboveWater(final Entity entity) {
        return isAboveWater(entity, false);
    }

    public static boolean isAboveWater(final Entity entity, final boolean packet) {
        if (entity == null) {
            return false;
        }
        final double y = entity.posY - (packet ? 0.03 : (isPlayer(entity) ? 0.2 : 0.5));
        for (int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); ++x) {
            for (int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (Wrapper.getWorld().getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) time);
    }
    public static List<Vec3d> getVarOffsetList(int x, int y, int z) {
        ArrayList<Vec3d> offsets = new ArrayList();
        offsets.add(new Vec3d(x, y, z));
        return offsets;
    }
    public static BlockPos getPlayerPos(EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }
    public static Vec3d[] getVarOffsets(int x, int y, int z) {
        List<Vec3d> offsets = getVarOffsetList(x, y, z);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }
    public static final BlockPos getFlooredPosition(Entity $this$flooredPosition) {
        return new BlockPos(floorToInt($this$flooredPosition.posX), floorToInt($this$flooredPosition.posY), floorToInt($this$flooredPosition.posZ));
    }
    public static final int floorToInt(double $this$floorToInt) {
        return (int)Math.floor($this$floorToInt);
    }
    private static Vec3d getEyesPos() {
        return new Vec3d(Wrapper.getPlayer().posX, Wrapper.getPlayer().posY + Wrapper.getPlayer().getEyeHeight(), Wrapper.getPlayer().posZ);
    }
    public static double[] calculateLookAt(final double n, final double n2, final double n3, final EntityPlayer entityPlayer,int x) {
        final Vec3d eyesPos = getEyesPos();
        final double n4 = n - eyesPos.x;
        final double n5 = n2 - eyesPos.y;
        final double n6 = n3 - eyesPos.z;
        return new double[]{Wrapper.getPlayer().rotationYaw + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(n6, n4)) - 90.0f - Wrapper.getPlayer().rotationYaw), Wrapper.getPlayer().rotationPitch + MathHelper.wrapDegrees((float) (-Math.toDegrees(Math.atan2(n5, Math.sqrt(n4 * n4 + n6 * n6)))) - Wrapper.getPlayer().rotationPitch)};
    }
    public static final Vec3d[] antiDropOffsetList = new Vec3d[]{new Vec3d(0.0, -2.0, 0.0)};
    public static final Vec3d[] platformOffsetList = new Vec3d[]{new Vec3d(0.0, -1.0, 0.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(1.0, -1.0, 0.0)};
    public static final Vec3d[] legOffsetList = new Vec3d[]{new Vec3d(-1.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0)};
    public static final Vec3d[] OffsetList = new Vec3d[]{new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(0.0, 2.0, 0.0)};
    public static final Vec3d[] antiStepOffsetList = new Vec3d[]{new Vec3d(-1.0, 2.0, 0.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(0.0, 2.0, -1.0)};
    public static final Vec3d[] antiScaffoldOffsetList = new Vec3d[]{new Vec3d(0.0, 3.0, 0.0)};
    public static final Vec3d[] doubleLegOffsetList = new Vec3d[]{new Vec3d(-1.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-2.0, 0.0, 0.0), new Vec3d(2.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -2.0), new Vec3d(0.0, 0.0, 2.0)};
    public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        return EntityUtil.getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop).size() == 0;
    }
    public static List<Vec3d> getUntrappedBlocks(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>();
        if (!antiStep && EntityUtil.getUnsafeBlocks(player, 2, false).size() == 4) {
            vec3ds.addAll(EntityUtil.getUnsafeBlocks(player, 2, false));
        }
        for (int i = 0; i < EntityUtil.getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop).length; ++i) {
            Vec3d vector = EntityUtil.getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop)[i];
            BlockPos targetPos = new BlockPos(player.getPositionVector()).add(vector.x, vector.y, vector.z);
            Block block = EntityUtil.mc.world.getBlockState(targetPos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
                continue;
            }
            vec3ds.add(vector);
        }
        return vec3ds;
    }

    public static List<Vec3d> getTrapOffsetsList(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        ArrayList<Vec3d> offsets = new ArrayList<Vec3d>(EntityUtil.getOffsetList(1, false));
        offsets.add(new Vec3d(0.0, 2.0, 0.0));
        if (antiScaffold) {
            offsets.add(new Vec3d(0.0, 3.0, 0.0));
        }
        if (antiStep) {
            offsets.addAll(EntityUtil.getOffsetList(2, false));
        }
        if (legs) {
            offsets.addAll(EntityUtil.getOffsetList(0, false));
        }
        if (platform) {
            offsets.addAll(EntityUtil.getOffsetList(-1, false));
            offsets.add(new Vec3d(0.0, -1.0, 0.0));
        }
        if (antiDrop) {
            offsets.add(new Vec3d(0.0, -2.0, 0.0));
        }
        return offsets;
    }
    public static Vec3d[] getTrapOffsets(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
        List<Vec3d> offsets = EntityUtil.getTrapOffsetsList(antiScaffold, antiStep, legs, platform, antiDrop);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }
    public static List<Vec3d> targets(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
        ArrayList<Vec3d> placeTargets = new ArrayList<Vec3d>();
        if (antiDrop) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiDropOffsetList));
        }
        if (platform) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, platformOffsetList));
        }
        if (legs) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, legOffsetList));
        }
        Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, OffsetList));
        if (antiStep) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiStepOffsetList));
        } else {
            List<Vec3d> vec3ds = EntityUtil.getUnsafeBlocksFromVec3d(vec3d, 2, false);
            if (vec3ds.size() == 4) {
                block5:
                for (Vec3d vector : vec3ds) {
                    BlockPos position = new BlockPos(vec3d).add(vector.x, vector.y, vector.z);
                    switch (BlockUtil.isPositionPlaceable(position, raytrace)) {
                        case 0: {
                            break;
                        }
                        case -1:
                        case 1:
                        case 2: {
                            continue block5;
                        }
                        case 3: {
                            placeTargets.add(vec3d.add(vector));
                            break;
                        }
                    }
                    if (antiScaffold) {
                        Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
                    }
                    return placeTargets;
                }
            }
        }
        if (antiScaffold) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
        }
        return placeTargets;
    }

    public static final boolean isEntityAlive(@NotNull Entity entity) {
        if (entity.isDead) return false;
        if (!(entity instanceof EntityLivingBase)) return true;
        float f = ((EntityLivingBase)entity).getHealth();
        boolean bl = false;
        if (Float.isNaN(f)) return true;
        if (!(((EntityLivingBase)entity).getHealth() > 0.0f)) return false;
        return true;
    }
    public static void LocalPlayerfakeJump() {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY +0.42, mc.player.posZ,true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.75, mc.player.posZ,true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY +1.01, mc.player.posZ,true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY +1.16, mc.player.posZ,true));
    }
    public static boolean isInLiquid() {
        if (mc.player.fallDistance >= 3.0f) {
            return false;
        }
        boolean inLiquid = false;
        final AxisAlignedBB bb = (mc.player.getRidingEntity() != null) ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
        final int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; ++x) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; ++z) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (!(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    inLiquid = true;
                }
            }
        }
        return inLiquid;
    }
    public static double getRelativeX(final float yaw) {
        return MathHelper.sin(-yaw * 0.017453292f);
    }

    public static double getRelativeZ(final float yaw) {
        return MathHelper.cos(yaw * 0.017453292f);
    }

    public static boolean isNeutralMob(final Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
    }
    public static boolean isHostileMob(final Entity entity) {
        return entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity);
    }
    public static boolean isOnLiquid(final double offset) {
        if (mc.player.fallDistance >= 3.0f) {
            return false;
        }
        final AxisAlignedBB bb = (mc.player.getRidingEntity() != null) ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0, 0.0, 0.0).offset(0.0, -offset, 0.0) : mc.player.getEntityBoundingBox().contract(0.0, 0.0, 0.0).offset(0.0, -offset, 0.0);
        boolean onLiquid = false;
        final int y = (int) bb.minY;
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0); ++x) {
            for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0); ++z) {
                final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != Blocks.AIR) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }
    public static boolean stopSneaking(boolean isSneaking) {
        if (isSneaking && EntityUtil.mc.player != null) {
            EntityUtil.mc.player.connection.sendPacket(new CPacketEntityAction(EntityUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        return false;
    }
    public static Vec3d getInterpolatedPos(final Entity entity, final float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
    }
    public static boolean isAboveLiquid(final Entity entity) {
        if (entity == null) {
            return false;
        }
        final double n = entity.posY + 0.01;
        for (int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); ++i) {
            for (int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); ++j) {
                if (mc.world.getBlockState(new BlockPos(i, (int) n, j)).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean checkCollide() {
        return !mc.player.isSneaking() && (mc.player.getRidingEntity() == null || mc.player.getRidingEntity().fallDistance < 3.0f) && mc.player.fallDistance < 3.0f;
    }
    public static boolean checkForLiquid(final Entity entity, final boolean b) {
        if (entity == null) {
            return false;
        }
        final double posY = entity.posY;
        double n;
        if (b) {
            n = 0.03;
        } else if (entity instanceof EntityPlayer) {
            n = 0.2;
        } else {
            n = 0.5;
        }
        final double n2 = posY - n;
        for (int i = MathHelper.floor(entity.posX); i < MathHelper.ceil(entity.posX); ++i) {
            for (int j = MathHelper.floor(entity.posZ); j < MathHelper.ceil(entity.posZ); ++j) {
                if (mc.world.getBlockState(new BlockPos(i, MathHelper.floor(n2), j)).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }
    public static void attackEntity(Entity entity, boolean packet) {
        if (packet) {
            mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            mc.playerController.attackEntity(mc.player, entity);
        }

    }
    public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
        if (packet) {
            EntityUtil.mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            EntityUtil.mc.playerController.attackEntity(EntityUtil.mc.player, entity);
        }
        if (swingArm) {
            EntityUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
    public static float getHealth(Entity entity) {
        if (EntityUtil.isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        }
        return 0.0f;
    }
    public static boolean getSurroundWeakness(Vec3d pos, int feetMine, int render) {
        switch (feetMine) {
            case 1: {
                Block blockb;
                Block blocka;
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX() - 2, raytrace.getY(), raytrace.getZ()) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)(raytrace.getX() - 2), (double)raytrace.getY(), (double)raytrace.getZ())) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, 1, 0)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || (blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, 0, 0)).getBlock()) != Blocks.AIR && blocka != Blocks.FIRE || (blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, -1, 0)).getBlock()) != Blocks.OBSIDIAN && blockb != Blocks.BEDROCK || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 0, 0)).getBlock() == Blocks.BEDROCK) {
                    break;
                }
                return true;
            }
            case 2: {
                Block blockb;
                Block blocka;
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX() + 2, raytrace.getY(), raytrace.getZ()) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)(raytrace.getX() + 2), (double)raytrace.getY(), (double)raytrace.getZ())) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, 1, 0)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || (blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, 0, 0)).getBlock()) != Blocks.AIR && blocka != Blocks.FIRE || (blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, -1, 0)).getBlock()) != Blocks.OBSIDIAN && blockb != Blocks.BEDROCK || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 0, 0)).getBlock() == Blocks.BEDROCK) {
                    break;
                }
                return true;
            }
            case 3: {
                Block blockb;
                Block blocka;
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX(), raytrace.getY(), raytrace.getZ() - 2) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)raytrace.getX(), (double)raytrace.getY(), (double)(raytrace.getZ() - 2))) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, -2)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || (blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -2)).getBlock()) != Blocks.AIR && blocka != Blocks.FIRE || (blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, -1, -2)).getBlock()) != Blocks.OBSIDIAN && blockb != Blocks.BEDROCK || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -1)).getBlock() == Blocks.BEDROCK) {
                    break;
                }
                return true;
            }
            case 4: {
                Block blockb;
                Block blocka;
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX(), raytrace.getY(), raytrace.getZ() + 2) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)raytrace.getX(), (double)raytrace.getY(), (double)(raytrace.getZ() + 2))) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, 2)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || (blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 2)).getBlock()) != Blocks.AIR && blocka != Blocks.FIRE || (blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, -1, 2)).getBlock()) != Blocks.OBSIDIAN && blockb != Blocks.BEDROCK || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 1)).getBlock() == Blocks.BEDROCK) {
                    break;
                }
                return true;
            }
            case 5: {
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX() - 1, raytrace.getY(), raytrace.getZ()) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)(raytrace.getX() - 1), (double)raytrace.getY(), (double)raytrace.getZ())) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 1, 0)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 0, 0)).getBlock() == Blocks.BEDROCK) {
                    break;
                }
                return true;
            }
            case 6: {
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX() + 1, raytrace.getY(), raytrace.getZ()) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)(raytrace.getX() + 1), (double)raytrace.getY(), (double)raytrace.getZ())) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 1, 0)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 0, 0)).getBlock() == Blocks.BEDROCK) {
                    break;
                }
                return true;
            }
            case 7: {
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX(), raytrace.getY(), raytrace.getZ() - 1) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)raytrace.getX(), (double)raytrace.getY(), (double)(raytrace.getZ() - 1))) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, -1)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -1)).getBlock() == Blocks.BEDROCK) {
                    break;
                }
                return true;
            }
            case 8: {
                BlockPos raytrace = new BlockPos(pos);
                if (!BlockUtil.canBlockBeSeen(raytrace.getX(), raytrace.getY(), raytrace.getZ() + 1) && Math.sqrt(EntityUtil.mc.player.getDistanceSq((double)raytrace.getX(), (double)raytrace.getY(), (double)(raytrace.getZ() + 1))) > 3.0) {
                    return false;
                }
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, 1)).getBlock();
                if (block != Blocks.AIR && block != Blocks.FIRE || EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 1)).getBlock() == Blocks.BEDROCK) {
                    break;
                }
                return true;
            }
        }
        switch (render) {
            case 1: {
                Block blockb;
                Block blocka;
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, 1, 0)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) {
                        return false;
                    }
                }
                if ((blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, 0, 0)).getBlock()) != Blocks.AIR) {
                    if (blocka != Blocks.FIRE) {
                        return false;
                    }
                }
                if ((blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-2, -1, 0)).getBlock()) != Blocks.OBSIDIAN) {
                    if (blockb != Blocks.BEDROCK) {
                        return false;
                    }
                }
                if (EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 0, 0)).getBlock() == Blocks.BEDROCK) {
                    return false;
                }
                return true;
            }
            case 2: {
                Block blockb;
                Block blocka;
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, 1, 0)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) {
                        return false;
                    }
                }
                if ((blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, 0, 0)).getBlock()) != Blocks.AIR) {
                    if (blocka != Blocks.FIRE) {
                        return false;
                    }
                }
                if ((blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(2, -1, 0)).getBlock()) != Blocks.OBSIDIAN) {
                    if (blockb != Blocks.BEDROCK) {
                        return false;
                    }
                }
                if (EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 0, 0)).getBlock() == Blocks.BEDROCK) {
                    return false;
                }
                return true;
            }
            case 3: {
                Block blockb;
                Block blocka;
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, -2)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) {
                        return false;
                    }
                }
                if ((blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -2)).getBlock()) != Blocks.AIR) {
                    if (blocka != Blocks.FIRE) {
                        return false;
                    }
                }
                if ((blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, -1, -2)).getBlock()) != Blocks.OBSIDIAN) {
                    if (blockb != Blocks.BEDROCK) {
                        return false;
                    }
                }
                if (EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -1)).getBlock() == Blocks.BEDROCK) {
                    return false;
                }
                return true;
            }
            case 4: {
                Block blockb;
                Block blocka;
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, 2)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) {
                        return false;
                    }
                }
                if ((blocka = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 2)).getBlock()) != Blocks.AIR) {
                    if (blocka != Blocks.FIRE) {
                        return false;
                    }
                }
                if ((blockb = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, -1, 2)).getBlock()) != Blocks.OBSIDIAN) {
                    if (blockb != Blocks.BEDROCK) {
                        return false;
                    }
                }
                if (EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 1)).getBlock() == Blocks.BEDROCK) {
                    return false;
                }
                return true;
            }
            case 5: {
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 1, 0)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) {
                        return false;
                    }
                }
                if (EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(-1, 0, 0)).getBlock() == Blocks.BEDROCK) {
                    return false;
                }
                return true;
            }
            case 6: {
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 1, 0)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) {
                        return false;
                    }
                }
                if (EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(1, 0, 0)).getBlock() == Blocks.BEDROCK) {
                    return false;
                }
                return true;
            }
            case 7: {
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, -1)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) {
                        return false;
                    }
                }
                if (EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, -1)).getBlock() == Blocks.BEDROCK) {
                    return false;
                }
                return true;
            }
            case 8: {
                Block block = EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 1, 1)).getBlock();
                if (block != Blocks.AIR) {
                    if (block != Blocks.FIRE) {
                        return false;
                    }
                }
                if (EntityUtil.mc.world.getBlockState(new BlockPos(pos).add(0, 0, 1)).getBlock() == Blocks.BEDROCK) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }
    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleBlocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }
        return circleBlocks;
    }
    public static List<BlockPos> getSphere2(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleBlocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        if(y + plus_y>cy)continue;
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }
        return circleBlocks;
    }
    public static boolean isPlayer(final Entity entity) {
        return entity instanceof EntityPlayer;
    }
    public static boolean isInHole(final Entity entity) {
        return isBlockValid(new BlockPos(entity.posX, entity.posY, entity.posZ));
    }
    public static boolean isInHole(final EntityPlayer entity) {
        return isBlockValid(new BlockPos(entity.posX, entity.posY, entity.posZ));
    }
    public static boolean basicChecksEntity(Entity pl) {
        return pl.getName().equals(mc.player.getName()) || FriendManager.isFriend(pl.getName()) || pl.isDead;
    }
    public static boolean isBlockValid(final BlockPos blockPos) {
        return isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos);
    }
    public static boolean isBothHole(final BlockPos blockPos) {
        final BlockPos[] array = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        for (final BlockPos pos : array) {
            final IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || (touchingState.getBlock() != Blocks.BEDROCK && touchingState.getBlock() != Blocks.OBSIDIAN)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isObbyHole(final BlockPos blockPos) {
        final BlockPos[] array = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        for (final BlockPos pos : array) {
            final IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
        }
        return true;
    }
    public static boolean isBedrockHole(final BlockPos blockPos) {
        final BlockPos[] array = new BlockPos[]{blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down()};
        for (final BlockPos pos : array) {
            final IBlockState touchingState = mc.world.getBlockState(pos);
            if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.BEDROCK) {
                return false;
            }
        }
        return true;
    }
    public static boolean isDead(Entity entity) {
        return !isAlive(entity);
    }
    public static boolean isAlive(Entity entity) {
        return (isLiving(entity) && !entity.isDead && ((EntityLivingBase) entity).getHealth() > 0.0F);
    }
    public static Minecraft mc = Minecraft.getMinecraft();
    public static boolean isSafe(Entity entity, int height, boolean floor, boolean face) {
        return getUnsafeBlocks(entity, height, floor, face).size() == 0;
    }
    public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor) {
        List<Vec3d> list = EntityUtil.getUnsafeBlocks(entity, height, floor,true);
        Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }
    public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height, boolean floor) {
        List<Vec3d> list = EntityUtil.getUnsafeBlocksFromVec3d(pos, height, floor,false);
        Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }

    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor, boolean face) {
        return getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor, face);
    }
    public static Vec3d[] getOffsets(int y, boolean floor, boolean face) {
        List<Vec3d> offsets = getOffsetList(y, floor, face);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }
    public static List<Vec3d> getOffsetList(int y, boolean floor, boolean face) {
        ArrayList<Vec3d> offsets = new ArrayList<Vec3d>();
        if (face) {
            offsets.add(new Vec3d(-1.0, y, 0.0));
            offsets.add(new Vec3d(1.0, y, 0.0));
            offsets.add(new Vec3d(0.0, y, -1.0));
            offsets.add(new Vec3d(0.0, y, 1.0));
        } else {
            offsets.add(new Vec3d(-1.0, y, 0.0));
        }
        if (floor) {
            offsets.add(new Vec3d(0.0, y - 1, 0.0));
        }
        return offsets;
    }
    public static boolean isSafe(Entity entity, int height, boolean floor) {
        if (EntityUtil.getUnsafeBlocks(entity, height, floor).size() != 0) {
            return false;
        }
        return true;
    }
    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>();
        for (Vec3d vector : EntityUtil.getOffsets(height, floor)) {
            BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            Block block = EntityUtil.mc.world.getBlockState(targetPos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
                continue;
            }
            vec3ds.add(vector);
        }
        return vec3ds;
    }
    public static List<Vec3d> getOffsetList(int y, boolean floor) {
        ArrayList<Vec3d> offsets = new ArrayList<Vec3d>();
        offsets.add(new Vec3d(-1.0, y, 0.0));
        offsets.add(new Vec3d(1.0, y, 0.0));
        offsets.add(new Vec3d(0.0, y, -1.0));
        offsets.add(new Vec3d(0.0, y, 1.0));
        if (floor) {
            offsets.add(new Vec3d(0.0, y - 1, 0.0));
        }
        return offsets;
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        List<Vec3d> offsets = EntityUtil.getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }
    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
        return EntityUtil.getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor);
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor, boolean face) {
        ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>();
        for (Vec3d vector : getOffsets(height, floor, face)) {
            BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            Block block = mc.world.getBlockState(targetPos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow))
                continue;
            vec3ds.add(vector);
        }
        return vec3ds;
    }
    public static EntityPlayer getClosestEnemy(double distance) {
        EntityPlayer closest = null;
        try {
            for (EntityPlayer player : mc.world.playerEntities) {
                if (isntValid(player, distance)) continue;
                if (closest == null) {
                    closest = player;
                    continue;
                }
                if (!(mc.player.getDistanceSq(player) < mc.player.getDistanceSq(closest))) continue;
                closest = player;
            }
        } catch (Exception ignored) {
        }
        return closest;
    }
    public static boolean isInWater(final Entity entity) {
        if (entity == null) {
            return false;
        }
        final double y = entity.posY + 0.01;
        for (int x = MathHelper.floor(entity.posX); x < MathHelper.ceil(entity.posX); ++x) {
            for (int z = MathHelper.floor(entity.posZ); z < MathHelper.ceil(entity.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, (int) y, z);
                if (Wrapper.getWorld().getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }
    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }
    public static boolean isMoving() {
        return mc.player.moveForward != 0.0 || mc.player.moveStrafing != 0.0;
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return EntityUtil.getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static boolean isPlayerInHole() {
        BlockPos blockPos = getLocalPlayerPosFloored();

        IBlockState blockState = mc.world.getBlockState(blockPos);

        if (blockState.getBlock() != Blocks.AIR)
            return false;

        if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR)
            return false;

        if (mc.world.getBlockState(blockPos.down()).getBlock() == Blocks.AIR)
            return false;

        final BlockPos[] touchingBlocks = new BlockPos[]
                {blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west()};

        int validHorizontalBlocks = 0;
        for (BlockPos touching : touchingBlocks) {
            final IBlockState touchingState = mc.world.getBlockState(touching);
            if ((touchingState.getBlock() != Blocks.AIR) && touchingState.isFullBlock())
                validHorizontalBlocks++;
        }

        return validHorizontalBlocks >= 4;
    }

    public static BlockPos getLocalPlayerPosFloored() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static boolean isFakeLocalPlayer(Entity entity) {
        return entity != null && entity.getEntityId() == -100 && mc.player != entity;
    }

    public static boolean isPassive(Entity e) {
        if (e instanceof EntityWolf && ((EntityWolf) e).isAngry()) {
            return false;
        }
        if (e instanceof EntityAgeable || e instanceof EntityAmbientCreature || e instanceof EntitySquid) {
            return true;
        }
        return e instanceof EntityIronGolem && ((EntityIronGolem) e).getRevengeTarget() == null;
    }

    public static boolean isLiving(Entity e) {
        return e instanceof EntityLivingBase;
    }

    public static float[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
        double dirX = me.posX - px;
        double dirY = me.posY - py;
        double dirZ = me.posZ - pz;
        double len = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        dirX /= len;
        dirY /= len;
        dirZ /= len;
        double pitch = Math.asin(dirY);
        double yaw = Math.atan2(dirZ, dirX);
        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;
        yaw += 90f;
        return new float[]{(float) yaw, (float) pitch};
    }


}
