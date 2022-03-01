package dev.cuican.staypro.module.modules.combat;


import com.mojang.realmsclient.gui.ChatFormatting;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.*;
import dev.cuican.staypro.utils.block.BlockUtil;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModuleInfo(name = "AutoTrap", category = Category.COMBAT,description = "Traps other players")
public class AutoTrap extends Module {

    public Setting<Integer> delay = setting("Delay", 50, 0, 250);
    public Setting<Integer> blocksPerPlace = setting("BlocksPerTick", 8, 1, 30);
    public Setting<Double> range = setting("range", 6.0, 1, 10.0);
    public Setting<Boolean> rotate = setting("Rotate", false);
    public Setting<Boolean> raytrace = setting("Raytrace", false);
    public Setting<Boolean> antiScaffold = setting("AntiScaffold", false);
    public Setting<Boolean> antiStep = setting("antiStep", false);
    public Setting<Boolean> noGhost = setting("Packet", false);
    private final Timer timer = new Timer();
    private final Map<BlockPos, Integer> retries = new HashMap<BlockPos, Integer>();
    private final Timer retryTimer = new Timer();
    public EntityPlayer target;
    private boolean didPlace = false;
    private boolean switchedItem;
    private boolean isSneaking;
    private int lastHotbarSlot;
    private int placements = 0;
    private BlockPos startPos = null;
    private boolean offHand = false;
    public static boolean isPlacing = false;

    @Override
    public void onEnable() {
        if (fullNullCheck()) {

            return;
        }
        startPos = EntityUtil.getRoundedBlockPos(mc.player);
        lastHotbarSlot = mc.player.inventory.currentItem;
        retries.clear();
    }


    @Override
    public void onTick() {
        if (fullNullCheck()) {
            disable();
            return;
        }
        doTrap();
    }

    @Override
    public String getModuleInfo() {
        if (target != null) {
            return target.getName();
        }
        return null;
    }
    @Override
    public void onDisable() {
        isPlacing = false;
        isSneaking = EntityUtil.stopSneaking(isSneaking);
    }
    private void doTrap() {

        if (check()) {
            return;
        }
        doStaticTrap();
        if (didPlace) {
            timer.reset();
        }
    }

    private void doStaticTrap() {
        List<Vec3d> placeTargets = EntityUtil.targets(target.getPositionVector(), antiScaffold.getValue(), antiStep.getValue(), false, false, false, raytrace.getValue());
        placeList(placeTargets);
    }

    private void placeList(List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        for (Vec3d vec3d3 : list) {
            BlockPos position = new BlockPos(vec3d3);
            int placeability = BlockUtil.isPositionPlaceable(position, raytrace.getValue());
            if (placeability == 1 && (retries.get(position) == null || retries.get(position) < 4)) {
                placeBlock(position);
                retries.put(position, retries.get(position) == null ? 1 : retries.get(position) + 1);
                retryTimer.reset();
                continue;
            }
            if (placeability != 3) {
                continue;
            }
            placeBlock(position);
        }
    }

    private boolean check() {
        if(startPos==null)disable();
        isPlacing = false;
        didPlace = false;
        placements = 0;
        int obbySlot2 = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        if (obbySlot2 == -1) {
            toggle();
        }
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);

        if (!startPos.equals(EntityUtil.getRoundedBlockPos(mc.player))) {
            disable();
            return true;
        }
        if (retryTimer.passedMs(2000L)) {
            retries.clear();
            retryTimer.reset();
        }
        if (obbySlot == -1) {
            ChatUtil.sendMessage("<" +  getModuleInfo() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
            disable();
            return true;
        }
        if (mc.player.inventory.currentItem != lastHotbarSlot && mc.player.inventory.currentItem != obbySlot) {
            lastHotbarSlot = mc.player.inventory.currentItem;
        }
        isSneaking = EntityUtil.stopSneaking(isSneaking);
        target = getTarget(range.getValue(), true);
        return target == null || !timer.passedMs(delay.getValue().intValue());
    }
    public static double turnIntoKpH(double input) {
        return (double) MathHelper.sqrt(input) * 71.2729367892;
    }

    private EntityPlayer getTarget(double range, boolean trapped) {

        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0) + 1.0;
        for (EntityPlayer player : mc.world.playerEntities) {
            if(player== mc.player)continue;
            double distTraveledLastTickX = player.posX - player.prevPosX;
            double distTraveledLastTickZ = player.posZ - player.prevPosZ;
            double playerSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
            if (EntityUtil.isntValid(player, range) || trapped && EntityUtil.isTrapped(player, antiScaffold.getValue(), antiStep.getValue(), false, false, false) || turnIntoKpH(playerSpeed)> 10.0) {
                continue;
            }
            if (target == null) {
                target = player;
                distance = mc.player.getDistanceSq(player);
                continue;
            }
            if (!(mc.player.getDistanceSq(player) < distance)) {
                continue;
            }
            target = player;
            distance = mc.player.getDistanceSq(player);
        }
        return target;
    }

    private void placeBlock(BlockPos pos) {
        if (placements < blocksPerPlace.getValue() && mc.player.getDistanceSq(pos) <= MathUtil.square(5.0)) {
            isPlacing = true;
            int originalSlot = mc.player.inventory.currentItem;
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            if (obbySlot == -1 && eChestSot == -1) {
                toggle();
            }
            if (rotate.getValue()) {
                mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
                mc.playerController.updateController();
                isSneaking = BlockUtil.placeBlock(pos, offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, rotate.getValue(), noGhost.getValue(), isSneaking);
                mc.player.inventory.currentItem = originalSlot;
                mc.playerController.updateController();
            } else {
                mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
                mc.playerController.updateController();
                isSneaking = BlockUtil.placeBlock(pos, offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, rotate.getValue(), noGhost.getValue(), isSneaking);
                mc.player.inventory.currentItem = originalSlot;
                mc.playerController.updateController();
            }
            didPlace = true;
            ++placements;
        }
    }
}

