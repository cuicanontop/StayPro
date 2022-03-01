package dev.cuican.staypro.module.modules.combat;


import dev.cuican.staypro.client.FriendManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.EventMotion;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.CrystalUtil;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.TargetUtils;
import dev.cuican.staypro.utils.block.BlockUtil;
import dev.cuican.staypro.utils.graphics.RenderUtils;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEmptyDrops;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//@Parallel(runnable = true)
@ModuleInfo(name = "PistonAura", category = Category.COMBAT, description = "Place PistonAura to kill enemies")
public class PistonAura extends Module {

    public static final BlockPos[] pistonoff = new BlockPos[]{
            /*y = -1*/
            new BlockPos(-1, -1, -1),
            new BlockPos(0, -1, -1),
            new BlockPos(1, -1, -1),
            new BlockPos(-1, -1, 0),
            new BlockPos(0, -1, 0),
            new BlockPos(1, -1, 0),
            new BlockPos(-1, -1, 1),
            new BlockPos(0, -1, 1),
            new BlockPos(1, -1, 1),
            /*y = 0*/
            new BlockPos(-1, 0, -1),
            new BlockPos(0, 0, -1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 1),
            new BlockPos(0, 0, 1),
            new BlockPos(1, 0, 1),
            /*y = 1*/
            new BlockPos(-1, 1, -1),
            new BlockPos(0, 1, -1),
            new BlockPos(1, 1, -1),
            new BlockPos(-1, 1, 0),
            new BlockPos(0, 1, 0),
            new BlockPos(1, 1, 0),
            new BlockPos(-1, 1, 1),
            new BlockPos(0, 1, 1),
            new BlockPos(1, 1, 1)
    };
    public Setting<Boolean> antiWeakness = setting("AntiWeakness", false);
    public Setting<Boolean> packetAntiWeak = setting("PacketAntiWeakness", false).whenTrue(antiWeakness);
    Setting<Integer> range = setting("Range", 5, 0, 15);
    Setting<Integer> delay1 = setting("ChangeDelay", 5, 0, 20);
    Setting<Integer> delay2 = setting("PlaceDelay", 2, 0, 100);
    Setting<Integer> min = setting("MinDamage", 21, 0, 100);
    Setting<Boolean> Tapr = setting("Tapr", false);
    Setting<Boolean> Burrows = setting("BurrowsDog", false);
    Setting<Boolean> ds = setting("Keep", false);
    Setting<Boolean> np = setting("nochat", false);
    Setting<Boolean> npc = setting("render", false);
    int progress = 0;
    EnumFacing facing;
    int sleep;
    List<PA> attackable;

    public static Color alpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    @Override
    public void onEnable() {
        progress = 0;
        attackable = new ArrayList<>();
        super.onEnable();
    }

    @Override
    public void onTick() {
        InventoryUtil.push();
        int pitem = InventoryUtil.pickItem(33, false);
        int cryst = InventoryUtil.pickItem(426, false);
        int powtem1 = InventoryUtil.pickItem(152, false);
        int powtem2 = InventoryUtil.pickItem(76, false);
        if (pitem == -1 || cryst == -1 || (powtem1 == -1 && powtem2 == -1)) {
            if (!np.getValue()) ChatUtil.printChatMessage("\u00A77[NOgodF] \u00A74Item Not Found ");
            if (!ds.getValue()) toggle();
            return;
        }
        if (!TargetUtils.findTarget(range.getValue())) return;
        Entity player = TargetUtils.currentTarget;
        if (FriendManager.isFriend(player)) return;
        BlockPos playerPos = new BlockPos(TargetUtils.currentTarget);
        if (Burrows.getValue()) {
            if (mc.world.getBlockState(playerPos).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(playerPos).getBlock() != Blocks.LAVA
                    && mc.world.getBlockState(playerPos).getBlock() != Blocks.FLOWING_LAVA
                    && mc.world.getBlockState(playerPos).getBlock() != Blocks.FLOWING_WATER
                    && mc.world.getBlockState(playerPos).getBlock() != Blocks.FIRE
                    && mc.world.getBlockState(playerPos).getBlock() != Blocks.WATER
                    && mc.world.getBlockState(playerPos).getBlock() != Blocks.LAVA) {
                if (!np.getValue()) ChatUtil.printChatMessage("\u00A77[NOgodF] \u00A74 He is buurow ");
                return;
            }
        }

        int range = this.range.getValue();
        if (Tapr.getValue() && EntityUtil.isInHole(TargetUtils.currentTarget)) {
            InventoryUtil.setSlot(InventoryUtil.pickItem(49, false));
            BlockUtil.doPlace(BlockUtil.isPlaceable(playerPos.offset(EnumFacing.EAST, 1), 0, true), false);
            BlockUtil.doPlace(BlockUtil.isPlaceable(playerPos.offset(EnumFacing.EAST, 1).offset(EnumFacing.UP), 0, true), false);
            BlockUtil.doPlace(BlockUtil.isPlaceable(playerPos.offset(EnumFacing.EAST, 1).offset(EnumFacing.UP, 2), 0, true), false);
            BlockUtil.doPlace(BlockUtil.isPlaceable(playerPos.offset(EnumFacing.UP, 2), 0, true), false);
        }


        if (attackable.isEmpty() || attackable.get(0).stage > delay1.getValue()) {
            attackable = new ArrayList<>();
            for (int dx = -range; dx <= range; dx++) {
                for (int dy = -range; dy <= range; dy++) {
                    for (int dz = -range; dz <= range; dz++) {
                        BlockPos pos = new BlockPos(mc.player).add(dx, dy, dz);
                        if (player.getDistanceSq(pos) > range * range) continue;
                        boolean b = false;
                        for (BlockPos off : pistonoff) {
                            if (mc.world.getBlockState(pos.add(off)).getBlock() instanceof BlockObsidian) {
                                b = true;
                                break;
                            }
                            if (mc.world.getBlockState(pos.add(off)).getBlock() instanceof BlockEmptyDrops) {
                                b = true;
                                break;
                            }
                        }
                        if (!b) continue;
                        double damage = CrystalUtil.getDamage(new Vec3d(pos).add(.5, 0, .5), TargetUtils.currentTarget);
                        if (damage < min.getValue()) continue;
                        PA pa = new PA(pos, damage);
                        if (!pa.canPA()) continue;
                        attackable.add(pa);
                    }
                }

                attackable.sort((a, b) -> {
                    if (a == null && b == null)
                        return 0;
                    assert a != null;
                    return Double.compare(b.damage, a.damage);

                });
            }
        }

        InventoryUtil.pop();
    }

    @Listener
    public void onMotion(EventMotion event) {
        InventoryUtil.push();
        int pitem = InventoryUtil.pickItem(33, false);
        int cryst = InventoryUtil.pickItem(426, false);
        int powtem1 = InventoryUtil.pickItem(152, false);
        int powtem2 = InventoryUtil.pickItem(76, false);
        if (pitem == -1 || cryst == -1 || (powtem1 == -1 && powtem2 == -1)) {
            if (!ds.getValue()) toggle();
            return;
        }
        if (!TargetUtils.findTarget(range.getValue())) return;


        if (!attackable.isEmpty()) {
            attackable.get(0).updatePA(event);
        }

        for (Entity et : mc.world.loadedEntityList) {
            if (et instanceof EntityEnderCrystal) {
                if (FriendManager.isFriend(et.getName())) continue;
                if (et.getDistance(mc.player) > range.getValue()) continue;
                int oldSlot = -1;
                if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS) && (!mc.player.isPotionActive(MobEffects.STRENGTH) || Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() < 1)) {
                    oldSlot = mc.player.inventory.currentItem;
                    for (int i = 0; i < 45; ++i) {
                        ItemStack stack = mc.player.inventory.getStackInSlot(i);
                        if (stack != ItemStack.EMPTY) {
                            if (stack.getItem() instanceof ItemSword) {
                                if (packetAntiWeak.getValue()) {
                                    mc.player.inventory.currentItem = i;
                                    mc.playerController.updateController();
                                } else {
                                    mc.player.inventory.currentItem = i;
                                }
                                break;
                            } else if (stack.getItem() instanceof ItemTool) {
                                if (packetAntiWeak.getValue()) {
                                    mc.player.inventory.currentItem = i;
                                    mc.playerController.updateController();
                                } else {
                                    mc.player.inventory.currentItem = i;
                                }
                                break;
                            }
                        }
                    }
                }

                mc.playerController.attackEntity(mc.player, et);
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
        }

        InventoryUtil.pop();
    }

    @Listener
    public void onRenderWorld(RenderEvent event) {
        if (!attackable.isEmpty() && npc.getValue()) {
            Color col = new Color(0xFFFFFF);
            RenderUtils.drawBlockBox(attackable.get(0).crystal, alpha(col, 0x20));
            RenderUtils.drawBlockBox(attackable.get(0).piston, alpha(col, 0x20));
            if (attackable.get(0).power != null)
                RenderUtils.drawBlockBox(attackable.get(0).power, alpha(col, 0x20));
            RenderUtils.drawBlockBox(attackable.get(0).crystal.offset(attackable.get(0).pistonFacing), alpha(new Color(0xffffff), 0x20));
        }
    }

    public EnumFacing getFacing(BlockPos position) {
        for (EnumFacing f : EnumFacing.values()) {
            if (f.getAxis() == Axis.Y) continue;
            BlockPos pos = new BlockPos(position).offset(f, 2);

            if (mc.world.isAirBlock(pos)) {
                return f;
            }
        }
        return null;
    }

    public EnumFacing rotateHantaigawa(EnumFacing f) {
        switch (f) {
            case WEST:
                return EnumFacing.EAST;

            case EAST:
                return EnumFacing.WEST;

            case SOUTH:
                return EnumFacing.NORTH;

            case NORTH:
                return EnumFacing.SOUTH;

            case UP:
                return EnumFacing.DOWN;

            case DOWN:
                return EnumFacing.UP;

            default:
                throw new IllegalStateException("Unable to get CCW facing of " + this);
        }
    }

    public class PA {

        public BlockPos pos;
        public BlockPos crystal;
        public BlockPos power;
        public EnumFacing pistonFacing;
        public BlockPos piston;
        public double damage;
        public int stage;

        public PA(BlockPos pos, double damage) {
            this.pos = pos;
            this.damage = damage;
            this.stage = 0;
        }

        public boolean canPA() {
            boolean isTorch = InventoryUtil.pickItem(76, false) != -1;
            double pist = .5;
            for (EnumFacing f : EnumFacing.values()) {
                BlockPos crypos = pos.offset(f);
                //check
                if (!mc.world.isAirBlock(crypos)) continue;
                if (!mc.world.isAirBlock(crypos.offset(EnumFacing.UP))) continue;
                if (!TargetUtils.canAttack(mc.player.getPositionVector().add(0, mc.player.getEyeHeight(), 0), new Vec3d(crypos).add(.5D, 1.7D, .5D)))
                    continue;
                if (!(mc.world.getBlockState(crypos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockObsidian) && !(mc.world.getBlockState(crypos.offset(EnumFacing.DOWN)).getBlock() instanceof BlockEmptyDrops))
                    continue;
                if (!mc.world.checkNoEntityCollision(Block.FULL_BLOCK_AABB.offset(crypos))) continue;
                if (mc.player.getDistanceSq((double) crypos.getX() + 0.5D, (double) crypos.getY() + 0.5D, (double) crypos.getZ() + 0.5D) >= 64.0D)
                    continue;
                //check2
                this.crystal = crypos;
                this.pistonFacing = rotateHantaigawa(f);
                if (pistonFacing == EnumFacing.DOWN) continue;
                if (!mc.world.isAirBlock(crypos.offset(pistonFacing))) continue;

                for (BlockPos off : pistonoff) {
                    BlockPos pispos = crystal.add(off);
                    if (pispos.equals(crypos)) continue;
                    if (crypos.offset(EnumFacing.UP).equals(pispos)) continue;
                    if (crypos.offset(pistonFacing).equals(pispos)) continue;
                    EnumFacing sfac = EnumFacing.getDirectionFromEntityLiving(pispos, mc.player);
                    if (sfac.getAxis() == Axis.Y) {
                        if (pistonFacing != sfac) continue;
                    }
                    if (pistonFacing.getAxis() == Axis.Y) {
                        if (pistonFacing != sfac) continue;
                    }
                    this.power = null;
                    if (mc.world.isBlockPowered(pispos)) {
                        if (BlockUtil.isPlaceable(pispos, 0, true) == null) continue;
                    } else {
                        for (EnumFacing fa : EnumFacing.values()) {
                            BlockPos powpos = pispos.offset(fa);
                            if (pispos.equals(powpos)) continue;
                            if (pispos.offset(pistonFacing).equals(powpos)) continue;
                            if (crypos.equals(powpos)) continue;
                            if (crypos.offset(EnumFacing.UP).equals(powpos)) continue;
                            if (mc.player.getDistanceSq((double) powpos.getX() + 0.5D, (double) powpos.getY() + 0.5D, (double) powpos.getZ() + 0.5D) >= 64.0D)
                                continue;
                            if (BlockUtil.isPlaceable(powpos, 0, true) == null) continue;

                            if (pistonFacing.getDirectionVec().getX() > 0 && powpos.getX() - pist > crypos.getX())
                                continue;
                            if (pistonFacing.getDirectionVec().getY() > 0 && powpos.getY() - pist > crypos.getY())
                                continue;
                            if (pistonFacing.getDirectionVec().getZ() > 0 && powpos.getZ() - pist > crypos.getZ())
                                continue;
                            if (pistonFacing.getDirectionVec().getX() < 0 && powpos.getX() + pist < crypos.getX())
                                continue;
                            if (pistonFacing.getDirectionVec().getY() < 0 && powpos.getY() + pist < crypos.getY())
                                continue;
                            if (pistonFacing.getDirectionVec().getZ() < 0 && powpos.getZ() + pist < crypos.getZ())
                                continue;
                            if (!mc.world.isAirBlock(powpos)) continue;
                            this.power = powpos;
                        }
                        if (power == null) continue;
                    }
                    if (mc.player.getDistanceSq((double) pispos.getX() + 0.5D, (double) pispos.getY() + 0.5D, (double) pispos.getZ() + 0.5D) >= 64.0D)
                        continue;
                    if (!mc.world.checkNoEntityCollision(Block.FULL_BLOCK_AABB.offset(pispos))) continue;
                    if (pistonFacing.getDirectionVec().getX() > 0 && pispos.getX() - pist > crypos.getX()) continue;
                    if (pistonFacing.getDirectionVec().getY() > 0 && pispos.getY() - pist > crypos.getY()) continue;
                    if (pistonFacing.getDirectionVec().getZ() > 0 && pispos.getZ() - pist > crypos.getZ()) continue;
                    if (pistonFacing.getDirectionVec().getX() < 0 && pispos.getX() + pist < crypos.getX()) continue;
                    if (pistonFacing.getDirectionVec().getY() < 0 && pispos.getY() + pist < crypos.getY()) continue;
                    if (pistonFacing.getDirectionVec().getZ() < 0 && pispos.getZ() + pist < crypos.getZ()) continue;
                    if (!mc.world.isAirBlock(pispos)) continue;
                    if (!mc.world.isAirBlock(pispos.offset(pistonFacing))) continue;
                    if (pispos.getY() < crystal.getY() && pistonFacing.getAxis() != Axis.Y) continue;
                    this.piston = pispos;
                    return true;
                }
            }
            return false;
        }
        @Listener
        public void updatePA(EventMotion event) {
            int obsiitem = InventoryUtil.pickItem(49, false);
            int pitem = InventoryUtil.pickItem(33, false);
            int powtem1 = InventoryUtil.pickItem(152, false);
            int powtem2 = InventoryUtil.pickItem(76, false);
            int cryst = InventoryUtil.pickItem(426, false);

            switch (pistonFacing) {

                case SOUTH:
                    event.yaw = 180;
                    event.pitch = 0;
                    break;
                case NORTH:
                    event.yaw = 0;
                    event.pitch = 0;
                    break;
                case EAST:
                    event.yaw = 90;
                    event.pitch = 0;
                    break;
                case WEST:
                    event.yaw = -90;
                    event.pitch = 0;
                    break;
                case UP:
                case DOWN:
                    event.pitch = 90;
                    break;

            }

            if (stage == delay2.getValue()) {
                InventoryUtil.setSlot(pitem);
                BlockUtil.doPlace(BlockUtil.isPlaceable(piston, 0, false), true);

                if (power != null) {
                    InventoryUtil.setSlot(powtem1);
                    InventoryUtil.setSlot(powtem2);
                    BlockUtil.doPlace(BlockUtil.isPlaceable(power, 0, false), true);
                }

                InventoryUtil.setSlot(pitem);
                BlockUtil.doPlace(BlockUtil.isPlaceable(piston, 0, false), true);

                InventoryUtil.setSlot(cryst);
                CrystalUtil.placeCrystal(crystal);

                if (power != null) {
                    InventoryUtil.setSlot(powtem1);
                    InventoryUtil.setSlot(powtem2);
                    BlockUtil.doPlace(BlockUtil.isPlaceable(power, 0, false), true);
                }
            }

            if (stage == delay2.getValue() + 1) {
                InventoryUtil.setSlot(cryst);
                mc.world.setBlockToAir(piston);
                if (power != null) {
                    mc.world.setBlockToAir(power);
                }
            }
            stage++;
        }
    }
}
