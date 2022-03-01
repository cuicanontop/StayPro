package dev.cuican.staypro.module.modules.combat;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.utils.Timer;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.block.BlockUtil;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import dev.cuican.staypro.utils.look;
import dev.cuican.staypro.utils.particles.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBed;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;


@ModuleInfo(name = "BedAura", category = Category.COMBAT, description = "Automatically places + detonates a bed.")
public class BedAura extends Module {

    public final Setting<String> attackMode = setting("Mode", "Own",listOf("Normal", "Own"));
    public final Setting<Integer>attackRange = setting("Attack Range", 4, 0, 10);
    public final Setting<Integer> breakDelay = setting("Break Delay", 1, 0, 20);
    public final Setting<Integer> placeDelay = setting("Place Delay", 1, 0, 20);
    public final Setting<Integer> targetRange = setting("Target Range", 7, 0, 16);
    public final Setting<Boolean> rotate = setting("Rotate", true);
    public final Setting<Boolean> disableNone = setting("Disable No Bed", false);
    public final Setting<Boolean> autoSwitch = setting("Switch", true);
    public final Setting<Boolean> GhostHand = setting("GhostHand", false).whenFalse(autoSwitch);
    public final Setting<Boolean> antiSuicide = setting("Anti Suicide", false);
    public final Setting<Integer> antiSuicideHealth = setting("Suicide Health", 14, 1, 36);
    public final Setting<Integer> minDamage = setting("Min Damage", 5, 1, 36);
    public final Setting<Boolean> swingArm = setting("swingArm", false);
    private boolean hasNone = false;
    private int oldSlot = -1;
    private final ArrayList<BlockPos> placedPos = new ArrayList<>();
    private final Timer breakTimer = new Timer();
    private final Timer placeTimer = new Timer();
    @Override
    public void onEnable() {
        hasNone = false;
        placedPos.clear();

        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        int bedSlot = InventoryUtil.findFirstItemSlot(ItemBed.class, 0, 8);

        if (mc.player.inventory.currentItem != bedSlot && bedSlot != -1 && autoSwitch.getValue()) {
            oldSlot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = bedSlot;
        } else if (bedSlot == -1) {
            hasNone = true;
        }
    }
    @Override
    public void onDisable() {
        placedPos.clear();

        if (mc.player == null || mc.world == null) {
            return;
        }

        if (autoSwitch.getValue() && mc.player.inventory.currentItem != oldSlot && oldSlot != -1) {
            mc.player.inventory.currentItem = oldSlot;
        }

        if (hasNone && disableNone.getValue()) ChatUtil.sendSpamlessMessage("No beds detected... BedAura turned OFF!");

        hasNone = false;
        oldSlot = -1;
    }
    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.player.dimension == 0) {
            disable();
            return;
        }

        int bedSlot = InventoryUtil.findFirstItemSlot(ItemBed.class, 0, 8);

        if (mc.player.inventory.currentItem != bedSlot && bedSlot != -1 && autoSwitch.getValue()) {
            oldSlot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = bedSlot;
        } else if (bedSlot == -1) {
            hasNone = true;
        }

        if (antiSuicide.getValue() && (mc.player.getHealth() + mc.player.getAbsorptionAmount()) < antiSuicideHealth.getValue()) {
            return;
        }

        if (breakTimer.getTimePassed() / 50L >= breakDelay.getValue()) {
            breakTimer.reset();
            breakBed();
        }

        if (hasNone) {

            if (disableNone.getValue()) {
                disable();
                return;
            }

            return;
        }

        if (mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem() != Items.BED&&!GhostHand.getValue()) {
            return;
        }

        if (placeTimer.getTimePassed() / 50L >= placeDelay.getValue()) {
            placeTimer.reset();
            int solt = -1;
            if(GhostHand.getValue()&&bedSlot!=-1){
                solt = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = bedSlot;
                mc.playerController.updateController();
            }
            placeBed();
            if(GhostHand.getValue()&&solt!=-1){
                mc.player.inventory.currentItem = solt;
                mc.playerController.updateController();
            }
        }
    }

    private void breakBed() {
        for (TileEntity tileEntity : findBedEntities(mc.player)) {
            if (!(tileEntity instanceof TileEntityBed)) {
                continue;
            }

            if (rotate.getValue()) {
                look.lookAt(new Vec3d(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()));
            }

            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(tileEntity.getPos(), EnumFacing.UP, EnumHand.OFF_HAND, 0, 0, 0));
            return;
        }
    }

    private void placeBed() {
        for (EntityPlayer entityPlayer : findTargetEntities(mc.player)) {

            if (entityPlayer.isDead) {
                continue;
            }

            NonNullList<BlockPos> targetPos = findTargetPlacePos(entityPlayer);

            if (targetPos.size() < 1) {
                continue;
            }

            for (BlockPos blockPos : targetPos) {

                BlockPos targetPos1 = blockPos.up();

                if (targetPos1.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) > attackRange.getValue()) {
                    continue;
                }

                if (mc.world.getBlockState(targetPos1).getBlock() != Blocks.AIR) {
                    continue;
                }

                if (entityPlayer.getPosition() == targetPos1) {
                    continue;
                }

                if (DamageUtil.calculateDamage(targetPos1.getX(), targetPos1.getY(), targetPos1.getZ(), entityPlayer) < minDamage.getValue()) {
                    continue;
                }

//                if (ModuleManager.isModuleEnabled(AutoGG.class)) {
//                    AutoGG.INSTANCE.addTargetedPlayer(entityPlayer.getName());
//                }

                if (mc.world.getBlockState(targetPos1.east()).getBlock() == Blocks.AIR) {
                    placeBedFinal(targetPos1, 90, EnumFacing.DOWN);
                    return;
                } else if (mc.world.getBlockState(targetPos1.west()).getBlock() == Blocks.AIR) {
                    placeBedFinal(targetPos1, -90, EnumFacing.DOWN);
                    return;
                } else if (mc.world.getBlockState(targetPos1.north()).getBlock() == Blocks.AIR) {
                    placeBedFinal(targetPos1, 0, EnumFacing.DOWN);
                    return;
                } else if (mc.world.getBlockState(targetPos1.south()).getBlock() == Blocks.AIR) {
                    placeBedFinal(targetPos1, 180, EnumFacing.SOUTH);
                    return;
                }
            }
        }
    }

    private NonNullList<TileEntity> findBedEntities(EntityPlayer entityPlayer) {
        NonNullList<TileEntity> bedEntities = NonNullList.create();

        mc.world.loadedTileEntityList.stream()
            .filter(tileEntity -> tileEntity instanceof TileEntityBed)
            .filter(tileEntity -> tileEntity.getDistanceSq(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ) <= (attackRange.getValue() * attackRange.getValue()))
            .filter(this::isOwn)
            .forEach(bedEntities::add);

        bedEntities.sort(Comparator.comparing(tileEntity -> tileEntity.getDistanceSq(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ)));
        return bedEntities;
    }

    private boolean isOwn(TileEntity tileEntity) {
        if (attackMode.getValue().equalsIgnoreCase("Normal")) {
            return true;
        } else if (attackMode.getValue().equalsIgnoreCase("Own")) {
            for (BlockPos blockPos : placedPos) {
                if (blockPos.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) <= 3) {
                    return true;
                }
            }
        }

        return false;
    }

    private NonNullList<EntityPlayer> findTargetEntities(EntityPlayer entityPlayer) {
        NonNullList<EntityPlayer> targetEntities = NonNullList.create();

        mc.world.playerEntities.stream()
            .filter(entityPlayer1 -> !EntityUtil.basicChecksEntity(entityPlayer1))
            .filter(entityPlayer1 -> entityPlayer1.getDistance(entityPlayer) <= targetRange.getValue())
            .sorted(Comparator.comparing(entityPlayer1 -> entityPlayer1.getDistance(entityPlayer)))
            .forEach(targetEntities::add);

        return targetEntities;
    }

    private NonNullList<BlockPos> findTargetPlacePos(EntityPlayer entityPlayer) {
        NonNullList<BlockPos> targetPlacePos = NonNullList.create();

        targetPlacePos.addAll(EntityUtil.getSphere(mc.player.getPosition(), attackRange.getValue().floatValue(), attackRange.getValue().intValue(), false, true, 0)
            .stream()
            .filter(this::canPlaceBed)
            .sorted(Comparator.comparing(blockPos -> 1 - (DamageUtil.calculateDamage(blockPos.up().getX(), blockPos.up().getY(), blockPos.up().getZ(), entityPlayer))))
            .collect(Collectors.toList()));

        return targetPlacePos;
    }

    private boolean canPlaceBed(BlockPos blockPos) {
        if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR) {
            return false;
        }

        if (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR) {
            return false;
        }

        return mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos)).isEmpty();
    }
    @Override
    public String getModuleInfo() {
        return attackMode.getValue();
    }
    //bon55's bedAura really helped me understand how this all works
    private void placeBedFinal(BlockPos blockPos, int direction, EnumFacing enumFacing) {
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(direction, 0, mc.player.onGround));

        if (mc.world.getBlockState(blockPos).getBlock() != Blocks.AIR) {
            return;
        }

        BlockPos neighbourPos = blockPos.offset(enumFacing);
        EnumFacing oppositeFacing = enumFacing.getOpposite();

        Vec3d vec3d = new Vec3d(neighbourPos).add(0.5, 0.5, 0.5).add(new Vec3d(oppositeFacing.getDirectionVec()).scale(0.5));

        if (rotate.getValue()) {
            BlockUtil.faceVectorPacketInstant(vec3d, true);
        }

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbourPos, oppositeFacing, vec3d, EnumHand.MAIN_HAND);
        if(swingArm.getValue()&&vec3d!=null)mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        placedPos.add(blockPos);
    }
}