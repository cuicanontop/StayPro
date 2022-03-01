package dev.cuican.staypro.module.modules.combat;

import dev.cuican.staypro.client.FriendManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.utils.Timer;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.mixin.accessor.AccessorCPacketUseEntity;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.*;
import dev.cuican.staypro.utils.block.BlockUtil;
import dev.cuican.staypro.utils.graphics.BlockRenderSmooth;
import dev.cuican.staypro.utils.graphics.StayTessellator;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.List;
import java.util.*;


@ModuleInfo(name = "CevBreaker", category = Category.COMBAT,description = "Explosive head gets high damage")
public class CevBreaker extends Module {
    final List<Vec3d> placeTarget = new ArrayList<>();
    final List<Vec3d> Target = new ArrayList<>();
    public Setting<String> p = setting("Page", "CEV", listOf("CEV", "RENDER"));
    public Setting<Boolean> rotate = setting("Rotate", false).whenAtMode(p, "CEV");
    public Setting<Boolean> packet = setting("PacketPlace", false).whenAtMode(p, "CEV");
    public Setting<Boolean> GhostHand = setting("GhostHandPlace", true).whenAtMode(p, "CEV");;
    public Setting<Boolean> holeCheck = setting("HoleCheck", false).whenAtMode(p, "CEV");
    public Setting<Boolean> debug = setting("Debug", false).whenAtMode(p, "CEV");
    public Setting<Integer> HitDelay = setting("CrystalHitDelay", 45, 0, 500).whenAtMode(p, "CEV");
    public Setting<Integer> red = setting("Red", 255, 0, 255).whenAtMode(p, "RENDER");
    public Setting<Integer> green = setting("Green", 198, 0, 255).whenAtMode(p, "RENDER");
    public Setting<Integer> blue = setting("Blue", 203, 0, 255).whenAtMode(p, "RENDER");
    public Setting<Integer> alpha = setting("Alpha", 85, 0, 255).whenAtMode(p, "RENDER");
    public Setting<Boolean> rainbow = setting("Rainbow", true).whenAtMode(p, "RENDER");
    public Setting<Integer> RGBSpeed = setting("RGBSpeed", 1, 0, 255).whenTrue(rainbow).whenAtMode(p, "RENDER");
    public Setting<Float> Saturation = setting("Saturation", 0.65f, 0, 1).whenTrue(rainbow).whenAtMode(p, "RENDER");
    public Setting<Float> Brightness = setting("Brightness", 1f, 0, 1).whenTrue(rainbow).whenAtMode(p, "RENDER");
    public BlockRenderSmooth blockRenderSmooth = new BlockRenderSmooth(new BlockPos(0, 0, 0), 500);
    public FadeUtils fadeBlockSize = new FadeUtils(1200);
    public Timer BreakTimer = new Timer();
    public Timer MineTimer = new Timer();
    public Timer BoostTimer = new Timer();
    public Timer StageTimer = new Timer();
    public boolean canPlace = false;
    public boolean startMine = false;
    public boolean boostExplode = false;
    public BlockPos breakingPos;
    public BlockPos breakingPos2;
    public EntityEnderCrystal qwq;
    public EntityPlayer Players;
    public double qwqwq;
    public int slot = -1;
    public int r;
    public int g;
    public int b;
    public int offsetStep = 0;
    public int stage = 0;
    public Vec3d[] TargetHead = new Vec3d[]{
            new Vec3d(0.0, 3.0, 0.0)
    };
    public Vec3d[] Trap = new Vec3d[]{
            new Vec3d(0.0, 0.0, -1.0),
            new Vec3d(1.0, 0.0, 0.0),
            new Vec3d(0.0, 0.0, 1.0),
            new Vec3d(-1.0, 0.0, 0.0),
            new Vec3d(0.0, 1.0, -1.0),
            new Vec3d(1.0, 1.0, 0.0),
            new Vec3d(0.0, 1.0, 1.0),
            new Vec3d(-1.0, 1.0, 0.0),
            new Vec3d(0.0, 2.0, -1.0),
            new Vec3d(1.0, 2.0, 0.0),
            new Vec3d(0.0, 2.0, 1.0),
            new Vec3d(-1.0, 2.0, 0.0),
            new Vec3d(0.0, 3.0, -1.0)
    };




    public static EnumFacing rayTrace(BlockPos blockPos) {
        EnumFacing[] values;
        int length = (values = EnumFacing.values()).length;
        int i = 0;
        while (i < length) {
            EnumFacing enumFacing = values[i];
            Vec3d vec3d = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
            Vec3d vec3d2 = new Vec3d(blockPos.getX() + enumFacing.getDirectionVec().getX(), blockPos.getY() + enumFacing.getDirectionVec().getY(), blockPos.getZ() + enumFacing.getDirectionVec().getZ());
            RayTraceResult rayTraceBlocks;
            if ((rayTraceBlocks = mc.world.rayTraceBlocks(vec3d, vec3d2, false, true, false)) != null
                    && rayTraceBlocks.typeOfHit.equals(RayTraceResult.Type.BLOCK) && rayTraceBlocks.getBlockPos().equals(blockPos)) {
                return enumFacing;
            }
            i++;
        }
        if (blockPos.getY() > mc.player.posY + mc.player.getEyeHeight()) {
            return EnumFacing.DOWN;
        }
        return EnumFacing.UP;
    }

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        slot = mc.player.inventory.currentItem;
        if (findObi() == -1) {
            disable();
        }
        startMine = true;
        canPlace = false;
        boostExplode = false;
        fadeBlockSize.reset();
        stage = 0;
    }

    @Override
    public void onDisable() {
        BreakTimer.reset();
        MineTimer.reset();
        BoostTimer.reset();
        StageTimer.reset();
        fadeBlockSize.reset();
        breakingPos = null;
        breakingPos2 = null;
        cancelStart = false;
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketSpawnObject && BoostTimer.passed(45) && qwq != null && stage == 3 && boostExplode) {
            SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51) {
                try {
                    CPacketUseEntity wdnmd = new CPacketUseEntity();
                    ((AccessorCPacketUseEntity) wdnmd).setId(packet.getEntityID());
                    ((AccessorCPacketUseEntity) wdnmd).setAction(CPacketUseEntity.Action.ATTACK);
                    mc.player.connection.sendPacket(wdnmd);
                    BoostTimer.reset();
                } catch (ConcurrentModificationException ignored) {
                }
            }
        }
    }
    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (fullNullCheck()) {
            return;
        }
        if(!isEnabled()){
            return;
        }
        if (!(event.getPacket() instanceof CPacketPlayerDigging)) {
            return;
        }
        CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
        if (packet.getAction() != CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
            return;
        }
        event.setCancelled(this.cancelStart);
    }


    private boolean cancelStart = false;
    @Override
    public void onTick() {
        if (fullNullCheck()) {
            return;
        }
        slot = mc.player.inventory.currentItem;
        qwq = mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal).map(e -> (EntityEnderCrystal) e).min(Comparator.comparing(e -> mc.player.getDistance(e))).orElse(null);
        //Start

        if (stage == 0) {
            EntityPlayer player = findTarget();
            if (player == null) {
                disable();
                ChatUtil.printChatMessage("No Target!");
                return;
            }
            Players = player;
            if (InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE) == -1) {
                ChatUtil.printChatMessage("Can't find DIAMOND_PICKAXE in hotbar!");
                disable();
                return;
            }
            if (InventoryUtil.findHotbarBlock(BlockObsidian.class) == -1) {
                ChatUtil.printChatMessage("Can't find obsidian in hotbar!");
                disable();
                return;
            }
            if (InventoryUtil.getItemHotbar(Items.END_CRYSTAL) == -1&& mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                ChatUtil.printChatMessage("Can't find crystal in hotbar!");
                disable();
                return;
            }
            Collections.addAll(placeTarget, Trap);
            Collections.addAll(Target, TargetHead);
            if (offsetStep >= placeTarget.size()) {
                offsetStep = 0;
            }
            final BlockPos offset = new BlockPos(placeTarget.get(offsetStep));
            final BlockPos targetPos = new BlockPos(player.getPositionVector()).down().add(offset.getX(), offset.getY(), offset.getZ());
            final BlockPos awab = new BlockPos(Target.get(offsetStep));
            breakingPos = new BlockPos(player.getPositionVector()).down().add(awab.getX(), awab.getY(), awab.getZ());
            for (final Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos))) {
                if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                    break;
                }
            }
            qwqwq = CrystalUtil.calculateDamage(breakingPos.getX() + 0.5, breakingPos.getY() + 1, breakingPos.getZ() + 0.5, player);
            if (rotate.getValue()) {
                look.lookAt(new Vec3d(targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5));

            }
            if(breakingPos != null){

            }
            if (breakingPos != null && !canPlace && startMine) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
                if (mc.getConnection() != null) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, breakingPos, EnumFacing.UP));
                    this.cancelStart = true;
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakingPos, EnumFacing.UP));
                    breakingPos2 = breakingPos;
                }
            }
            if(!breakingPos.equals(breakingPos2)){
                this.cancelStart = false;
            }
            startMine = false;
            canPlace = true;
            if (player.moveForward >= 0.3 || player.moveStrafing >= 0.3) {
                toggle();
                return;
            }
            //Check For Target Is In Hole?
            if (holeCheck.getValue()) {
                if (EntityUtil.isInHole(player)) {
                    //Place Obi
                    if (canPlace) {
                        InventoryUtil.switchToHotbarSlot(InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN) , false);
                        BlockUtil.placeBlock(targetPos, EnumHand.MAIN_HAND ,rotate.getValue(), packet.getValue() , mc.player.isSneaking());
                        if (MineTimer.passed(250)) {
                            BlockUtil.placeBlock(breakingPos, EnumHand.MAIN_HAND , rotate.getValue(), packet.getValue() , mc.player.isSneaking());
                            MineTimer.reset();
                        }
                        InventoryUtil.switchToHotbarSlot(slot , false);
                    }
                } else {
                    return;
                }
            } else {
                //Place Obi
                if (canPlace) {
                    InventoryUtil.switchToHotbarSlot(InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN) , false);
                    BlockUtil.placeBlock(targetPos, EnumHand.MAIN_HAND ,rotate.getValue(), packet.getValue() , mc.player.isSneaking());
                    if (MineTimer.passed(250)) {
                        BlockUtil.placeBlock(breakingPos, EnumHand.MAIN_HAND , rotate.getValue(), packet.getValue() , mc.player.isSneaking());
                        MineTimer.reset();
                    }
                    InventoryUtil.switchToHotbarSlot(slot , false);
                }
            }
            blockRenderSmooth.setNewPos(breakingPos);
            offsetStep++;
            if (debug.getValue()) {
                ChatUtil.sendNoSpamMessage(String.valueOf(stage));
            }
            canPlace = false;
            stage = 1;
            return;
        }
        //Stage 1
        if (stage == 1) {
            //Get Offhand Crystal
            boolean offhand = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
            //Place Crystal Rotate
            if (rotate.getValue() && breakingPos != null) {
                look.lookAt(new Vec3d(breakingPos.getX() + 0.5, breakingPos.getY() + 1, breakingPos.getZ() + 0.5));
            }
            if (breakingPos != null) {
                //GhostHand
                if (!offhand && GhostHand.getValue()) {
                    int CrystalSlot = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
                    if (CrystalSlot != -1) {
                        InventoryUtil.switchToHotbarSlot(CrystalSlot, false);
                    }
                }
                EnumFacing nmsl = rayTrace(breakingPos);
                //Place Crystal
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(breakingPos, nmsl, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                InventoryUtil.switchToHotbarSlot(slot , false);
            }
            if (debug.getValue()) {
                ChatUtil.sendNoSpamMessage(String.valueOf(stage));
            }
            stage = 2;
            return;
        }
        //Stage 2 For Mining Obi
        if (BlockUtil.canBreak(breakingPos) && stage == 2) {
            int pickaxe = InventoryUtil.findHotbarItem(Items.DIAMOND_PICKAXE);
            if (mc.getConnection() != null) {
                if (GhostHand.getValue()) {
                    InventoryUtil.switchToHotbarSlot(pickaxe, false);
                    mc.playerController.updateController();
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, breakingPos, EnumFacing.UP));
                    InventoryUtil.switchToHotbarSlot(slot , false);
                    mc.playerController.updateController();
                }
            }
            if (debug.getValue()) {
                ChatUtil.sendNoSpamMessage(String.valueOf(stage));
            }
            canPlace = true;
            stage = 3;
            return;
        }
        //Stage 3
        if (stage == 3) {
            if (qwq != null) {
                BlockPos c = new BlockPos(qwq);
                //Rotate For Exploding
                if (rotate.getValue()) {
                    look.lookAt(new Vec3d(c.getX() + 0.5, c.getY() + 1, c.getZ() + 0.5));
                }
                if (breakingPos == null) {
                    canPlace = false;
                }
                //Explode Crystal
                if (BreakTimer.passed(HitDelay.getValue()) && mc.getConnection() != null && canPlace) {
                    if (qwqwq > 2) {
                        boostExplode = true;
                        mc.player.connection.sendPacket(new CPacketUseEntity(qwq));
                    }
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    BreakTimer.reset();
                    canPlace = false;
                }
            }
            if (qwq != null) {
                if (qwq.isDead) {
                    canPlace = false;
                    if (debug.getValue()) {
                        ChatUtil.sendNoSpamMessage(String.valueOf(stage));
                    }
                }
            }
        }
        boostExplode = false;
        stage = 0;
    }

    @Override
    public void onRenderWorld(RenderEvent event) {
        int hsBtoRGB = Color.HSBtoRGB((new float[]{
                System.currentTimeMillis() % 11520L / 11520.0f * RGBSpeed.getValue()
        })[0], Saturation.getValue(), Brightness.getValue());
        r = (hsBtoRGB >> 16 & 0xFF);
        g = (hsBtoRGB >> 8 & 0xFF);
        b = (hsBtoRGB & 0xFF);
        if (breakingPos != null) {
            Vec3d interpolateEntity = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
            AxisAlignedBB pos = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D).offset(blockRenderSmooth.getRenderPos());
            pos = pos.grow(0.0020000000949949026).offset(-interpolateEntity.x, -interpolateEntity.y, -interpolateEntity.z);
            renderESP(pos, (float) fadeBlockSize.easeOutQuad());
        } else {
            fadeBlockSize.reset();
        }
    }

    public void renderESP(AxisAlignedBB axisAlignedBB, float size) {
        double centerX = axisAlignedBB.minX + ((axisAlignedBB.maxX - axisAlignedBB.minX) / 2);
        double centerY = axisAlignedBB.minY + ((axisAlignedBB.maxY - axisAlignedBB.minY) / 2);
        double centerZ = axisAlignedBB.minZ + ((axisAlignedBB.maxZ - axisAlignedBB.minZ) / 2);
        double full = (axisAlignedBB.maxX - centerX);
        double progressValX = full * size;
        double progressValY = full * size;
        double progressValZ = full * size;

        AxisAlignedBB axisAlignedBB1 = new AxisAlignedBB(centerX - progressValX, centerY - progressValY, centerZ - progressValZ, centerX + progressValX, centerY + progressValY, centerZ + progressValZ);

        int a = 15;
        int c = alpha.getValue();
        if (c < 240) {
            c = a + c;
        }
        if (breakingPos != null) {
            StayTessellator.drawBoxTest((float) axisAlignedBB1.minX, (float) axisAlignedBB1.minY, (float) axisAlignedBB1.minZ, (float) axisAlignedBB1.maxX - (float) axisAlignedBB1.minX, (float) axisAlignedBB1.maxY - (float) axisAlignedBB1.minY, (float) axisAlignedBB1.maxZ - (float) axisAlignedBB1.minZ, (rainbow.getValue()) ? r : (red.getValue()), (rainbow.getValue()) ? g : (green.getValue()), (rainbow.getValue()) ? b : (blue.getValue()), c, 63);
            StayTessellator.drawBoundingBox(axisAlignedBB1, 1.5f, (rainbow.getValue()) ? r : (red.getValue()), (rainbow.getValue()) ? g : (green.getValue()), (rainbow.getValue()) ? b : (blue.getValue()), 255);
        }

    }

    public int findObi() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block instanceof BlockObsidian)
                    return i;
            }
        }
        return -1;
    }

    public EntityPlayer findTarget() {
        if (mc.world.playerEntities.isEmpty())
            return null;
        EntityPlayer closestTarget = null;
        for (final EntityPlayer target : mc.world.playerEntities) {
            if (target == mc.player) {
                continue;
            }
            if (FriendManager.isFriend(target.getName())) {
                continue;
            }
            if (target.getHealth() <= 0.0f) {
                continue;
            }
            if (closestTarget != null) {
                if (mc.player.getDistance(target) > mc.player.getDistance(closestTarget))
                    continue;
            }
            closestTarget = target;
        }
        return closestTarget;
    }
    @Override
    public String getModuleInfo() {
        return p.getValue();
    }


}
