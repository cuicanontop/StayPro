/*
 * Decompiled with CFR 0.151.
 *
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.BlockAir
 *  net.minecraft.block.BlockDeadBush
 *  net.minecraft.block.BlockEnderChest
 *  net.minecraft.block.BlockFire
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.block.BlockSnow
 *  net.minecraft.block.BlockTallGrass
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package dev.cuican.staypro.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.utils.Timer;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.block.BlockUtil;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import dev.cuican.staypro.utils.math.InfoCalculator;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.*;
@ModuleInfo(name = "Surround", category = Category.COMBAT, description = "Surrounds you with Obsidian")
public class Surround
        extends Module {


    public Setting<Integer> blocksPerTick = setting("BlocksPerTick", 12, 1, 20);
    public Setting<Integer> delay = setting("Delay", 0, 0, 250);

    public Setting<Boolean> togg1e = setting("AutoToggle", false);
    public Setting<Boolean> noGhost = setting("PacketPlace", false);
    public Setting<Boolean> center = setting("TPCenter", false);
    public Setting<Boolean> rotate = setting("Rotate", false);
    private final Timer timer = new Timer();
    private final Timer retryTimer = new Timer();
    private final Set<Vec3d> extendingBlocks = new HashSet<Vec3d>();
    private final Map<BlockPos, Integer> retries = new HashMap<BlockPos, Integer>();
    private boolean isSafe;
    private BlockPos startPos;
    private boolean didPlace = false;
    private int lastHotbarSlot;
    private boolean isSneaking;
    private int placements = 0;
    private int extenders = 1;
    private int obbySlot = -1;
    private boolean offHand = false;

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        this.lastHotbarSlot = mc.player.inventory.currentItem;
        this.startPos = new BlockPos(mc.player.posX,mc.player.posY,mc.player.posZ);
        if (this.center.getValue()) {
        setPositionPacket((double)this.startPos.getX() + 0.5, this.startPos.getY(), (double)this.startPos.getZ() + 0.5, true, true);
        }
        this.retries.clear();
        this.retryTimer.reset();
    }

    public void setPositionPacket(double x, double y, double z, boolean onGround, boolean setPos) {
       mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, onGround));
        if (setPos) {
           mc.player.setPosition(x, y, z);
        }
    }
    @Override
    public void onTick() {
        if (fullNullCheck()) {
            return;
        }
        this.doFeetPlace();
    }
    @Override
    public void onDisable() {
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    }
    @Override
    public String getModuleInfo() {
        if (!this.isSafe) {
            return ChatFormatting.GREEN + "Safe";
        }

        return ChatFormatting.RED + "Unsafe";
    }

    private void doFeetPlace() {
        if (this.check()) {
            return;
        }
        if (!EntityUtil.isSafe(mc.player, 0, true)) {
            this.isSafe = true;
            this.placeBlocks(mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray((Entity)mc.player, 0, true), true, false, false);
        } else {
            this.isSafe = false;
        }
        this.processExtendingBlocks();
        if (this.didPlace) {
            this.timer.reset();
        }
        if (this.togg1e.getValue()) {
            this.toggle();
        }

    }

    private void processExtendingBlocks() {
        if (this.extendingBlocks.size() == 2 && this.extenders < 1) {
            Vec3d[] array = new Vec3d[2];
            int i = 0;
            for (Vec3d extendingBlock : this.extendingBlocks) {
                array[i] = extendingBlock;
                ++i;
            }
            int placementsBefore = this.placements;
            if (this.areClose(array) != null) {
                this.placeBlocks(this.areClose(array), EntityUtil.getUnsafeBlockArrayFromVec3d(this.areClose(array), 0, true), true, false, true);
            }
            if (placementsBefore >= this.placements) {
                return;
            }
            this.extendingBlocks.clear();
            return;
        }
        if (this.extendingBlocks.size() <= 2) {
            if (this.extenders < 1) {
                return;
            }
        }
        this.extendingBlocks.clear();
    }
    public double speedometerCurrentSpeed = 0.0;
    private Vec3d areClose(Vec3d[] vec3ds) {
        int matches = 0;
        Vec3d[] vec3dArray = vec3ds;
        int n = vec3dArray.length;
        int n2 = 0;
        while (true) {
            if (n2 >= n) {
                if (matches != 2) {
                    return null;
                }
                return mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1]));
            }
            Vec3d vec3d = vec3dArray[n2];
            for (Vec3d pos : EntityUtil.getUnsafeBlockArray(mc.player, 0, true)) {
                if (!vec3d.equals(pos)) {
                    continue;
                }
                ++matches;
            }
            ++n2;
        }
    }
    public double turnIntoKpH(double input) {
        return (double) MathHelper.sqrt(input) * 71.2729367892;
    }
    public double getSpeedKpH() {
        double distTraveledLastTickX = mc.player.posX - mc.player.prevPosX;
        double distTraveledLastTickZ = mc.player.posZ - mc.player.prevPosZ;
         speedometerCurrentSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;

        double speedometerkphdouble = this.turnIntoKpH(speedometerCurrentSpeed);
        speedometerkphdouble = (double) Math.round(10.0 * speedometerkphdouble) / 10.0;
        return speedometerkphdouble;
    }
    private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
        boolean gotHelp = true;
        Vec3d[] vec3dArray = vec3ds;
        int n = vec3dArray.length;
        int n2 = 0;
        while (n2 < n) {
            Vec3d vec3d = vec3dArray[n2];
            gotHelp = true;
            BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            switch (BlockUtil.isPositionPlaceable(position, false)) {
                case 1: {
                    if (this.retries.get(position) == null || this.retries.get(position) < 4) {
                        this.placeBlock(position);
                        this.retries.put(position, this.retries.get(position) == null ? 1 : this.retries.get(position) + 1);
                        this.retryTimer.reset();
                        break;
                    }
                    if (getSpeedKpH() != 0.0 || isExtending || this.extenders >= 1) {
                        break;
                    }
                    this.placeBlocks(mc.player.getPositionVector().add(vec3d), EntityUtil.getUnsafeBlockArrayFromVec3d(mc.player.getPositionVector().add(vec3d), 0, true), hasHelpingBlocks, false, true);
                    this.extendingBlocks.add(vec3d);
                    ++this.extenders;
                    break;
                }
                case 2: {
                    if (!hasHelpingBlocks) {
                        break;
                    }
                    gotHelp = this.placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
                }
                case 3: {
                    if (gotHelp) {
                        this.placeBlock(position);
                    }
                    if (isHelping) {
                        return true;
                    }
                    break;
                }
            }
            ++n2;
        }
        return false;
    }

    private boolean check() {
        if (fullNullCheck()) {
            this.disable();
            return true;
        }
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (obbySlot == -1 && eChestSot == -1) {
            this.toggle();
        }
        this.offHand = InventoryUtil.isBlock(mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
        this.didPlace = false;
        this.extenders = 1;
        this.placements = 0;
        this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);

        if (this.retryTimer.passedMs(2500L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        if (this.obbySlot == -1 && !this.offHand && echestSlot == -1) {
            ChatUtil.sendMessage("<" + getModuleInfo() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
            this.disable();
            return true;
        }
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != this.obbySlot && mc.player.inventory.currentItem != echestSlot) {
            this.lastHotbarSlot = mc.player.inventory.currentItem;
        }
        if (!this.startPos.equals(new BlockPos(mc.player.posX,mc.player.posY,mc.player.posZ))) {
            this.disable();
            return true;
        }
        return !this.timer.passedMs(this.delay.getValue());
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements >= this.blocksPerTick.getValue()) {
            return;
        }
        int originalSlot = mc.player.inventory.currentItem;
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (obbySlot == -1 && eChestSot == -1) {
            this.toggle();
        }
        mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
        mc.playerController.updateController();
        for (BlockPos blockPos : new BlockPos[]{pos.north(), pos.south(), pos.east(), pos.west(), pos.down(), pos.up()}) {
            IBlockState block = mc.world.getBlockState(blockPos);
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
                continue;
            }
            this.isSneaking = BlockUtil.placeBlock(pos.down(), this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.rotate.getValue(), this.noGhost.getValue(), this.isSneaking);
        }
        this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.rotate.getValue(), this.noGhost.getValue(), this.isSneaking);
        mc.player.inventory.currentItem = originalSlot;
        mc.playerController.updateController();
        this.didPlace = true;
        ++this.placements;
    }
}

