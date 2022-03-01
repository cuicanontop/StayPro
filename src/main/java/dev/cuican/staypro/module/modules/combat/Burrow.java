/*
 * Decompiled with CFR 0.151.
 *
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package dev.cuican.staypro.module.modules.combat;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.network.MotionUpdateEvents;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.mixin.accessor.AccessorCPacketUseEntity;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.module.pingbypass.util.StopWatch;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.RotationUtil;
import dev.cuican.staypro.utils.block.BlockUtil;
import dev.cuican.staypro.utils.block.SpecialBlocks;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import dev.cuican.staypro.utils.math.RayTraceUtil;
import dev.cuican.staypro.utils.particles.DamageUtil;
import dev.cuican.staypro.utils.position.PositionUtil;
import dev.cuican.staypro.utils.thread.Locks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import java.util.*;
import java.util.stream.Collectors;

@ModuleInfo(name = "Burrow", category = Category.COMBAT, description = "XD")
public class Burrow
extends Module {
     private Setting<Boolean> breakCrystal = setting("BreakCrystal", true);
      private Setting<Boolean> antiWeakness = setting("AntiWeakness", true).whenTrue(breakCrystal);
     private Setting<Boolean> echest = setting("Echest", false);
    private Setting<Boolean> wait = setting("wait", true);
    private Setting<Boolean> bypass = setting("Bypass", false);
    private Setting<Double>bypassOffset =setting("BypassOffset",0.032,0.001,0.1).whenTrue(bypass);
    private Setting<Double>scaleFactor =setting("Factor",1.0,1.0,10.0).whenTrue(bypass);
    public Setting<Boolean> centers = setting("TPCenter", false);
    private final StopWatch scaleTimer = new StopWatch();
    private final StopWatch timer = new StopWatch();
    private double motionY;
    private BlockPos startPos;
    @Override
    public void onEnable()
    {
        timer.setTime(0);
        super.onEnable();
        if (mc.world == null || mc.player == null)
        {
            return;
        }
        startPos = getPlayerPos();
        if (this.centers.getValue()) {
            setPositionPacket((double)this.startPos.getX() + 0.5, this.startPos.getY(), (double)this.startPos.getZ() + 0.5, true, true);
        }
        startPos = getPlayerPos();
        if (singlePlayerCheck(startPos))
        {
            this.disable();
        }
    }
    public void setPositionPacket(double x, double y, double z, boolean onGround, boolean setPos) {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, onGround));
        if (setPos) {
            mc.player.setPosition(x, y, z);
        }
    }
    @Listener
    private final void onMotionUpdate(MotionUpdateEvents event) {
        if(event.getStage() == 0&&isInsideBlock()){
            if (bypass.getValue()) {
                event.setY(event.getY() - bypassOffset.getValue());
                event.setOnGround(false);
            }
        }
        if (!timer.passed(100)
               ) {
            return;
        }

        if (wait.getValue()) {
            BlockPos currentPos = getPlayerPos();
            if (!currentPos.equals(startPos)) {
                disable();
                return;
            }
        }
        if (isInsideBlock()) {
            return;
        }
        EntityPlayer rEntity = mc.player;
        BlockPos pos = PositionUtil.getPosition(rEntity);
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            if (!wait.getValue())
                disable();
            return;
        }

        BlockPos posHead = PositionUtil.getPosition(rEntity).up().up();
        if (!mc.world.getBlockState(posHead).getMaterial().isReplaceable()
                && wait.getValue()) {
            return;
        }


        CPacketUseEntity attacking = null;
        boolean crystals = false;
        float currentDmg = Float.MAX_VALUE;
        if (breakCrystal.getValue()){
            for (Entity crystal : mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal && !e.isDead).sorted(Comparator.comparing(e -> mc.player.getDistance(e))).collect(Collectors.toList())) {
                if (crystal instanceof EntityEnderCrystal){
                    mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                }
            }
        }
        for (Entity entity : mc.world.getEntitiesWithinAABB(
                Entity.class, new AxisAlignedBB(pos))) {
            if (entity != null
                    && !rEntity.equals(entity)
                    && !mc.player.equals(entity)
                    && !EntityUtil.isDead(entity)
                    && entity.preventEntitySpawning) {
                if (entity instanceof EntityEnderCrystal
                        && breakCrystal.getValue()) {
                    int oldSlot =  mc.player.inventory.currentItem;
                    if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS) && (!mc.player.isPotionActive(MobEffects.STRENGTH) || Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() < 1)) {
                        int i = findAntiWeakness();
                        if(i!=-1) mc.player.inventory.currentItem =i;
                    }
                    mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                    if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS) && (!mc.player.isPotionActive(MobEffects.STRENGTH) || Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() < 1)) {
                        mc.player.inventory.currentItem = oldSlot;
                    }
                    continue;
                }
                if (!wait.getValue())
                   disable();
                return;
            }
        }
        int weaknessSlot = -1;

        int slot;
        if(echest.getValue()
                || mc.world.getBlockState(pos.down())
                .getBlock() == Blocks.ENDER_CHEST){
            slot =   InventoryUtil.findHotbarBlock(Blocks.ENDER_CHEST,
                    Blocks.OBSIDIAN);
        }else {
            slot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN,
                    Blocks.ENDER_CHEST);
        }


        if (slot == -1)
        {
            return;
        }
        EnumFacing f = BlockUtil.getFacing(pos);
        if (f == null)
        {
            if (!wait.getValue())
            {
                disable();
            }

            return;
        }
        double y = applyScale(getY(rEntity,0));
        if (Double.isNaN(y)) {
            return;
        }
        BlockPos on = pos.offset(f);
        float[] r =
                RotationUtil.getRotations(on, f.getOpposite(), rEntity);
        RayTraceResult result =
                RayTraceUtil.getRayTraceResultWithEntity(r[0], r[1], rEntity);

        float[] vec = RayTraceUtil.hitVecToPlaceVec(on, result.hitVec);
        boolean sneaking = !SpecialBlocks.shouldSneak(on, true);

        EntityPlayer finalREntity = rEntity;
        int finalWeaknessSlot = weaknessSlot;
        CPacketUseEntity finalAttacking = attacking;
        if (singlePlayerCheck(pos))
        {
            if (!wait.getValue() )
                disable();
            return;
        }

        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
        {
        int lastSlot = mc.player.inventory.currentItem;




            doY(
                    finalREntity, finalREntity.posY + 0.42, true);
            doY(
                    finalREntity, finalREntity.posY + 0.75,true);
            doY(
                    finalREntity, finalREntity.posY + 1.01,true);
            doY(
                    finalREntity, finalREntity.posY + 1.16,true);




            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
            if (!sneaking) {
                mc.player.connection.sendPacket(
                        new CPacketEntityAction(mc.player,
                                CPacketEntityAction.Action.START_SNEAKING));
            }

            place(on, f.getOpposite(), slot, vec[0], vec[1], vec[2]);



            swing(slot);

            mc.player.inventory.currentItem = lastSlot;
            mc.playerController.updateController();


        });
        if (!sneaking) {
            mc.player.connection.sendPacket(
                    new CPacketEntityAction(mc.player,
                            CPacketEntityAction.Action.STOP_SNEAKING));
        }

        doY(rEntity, y, false);
      timer.reset();
        if (!wait.getValue())

           disable();
    }

    public static void swing(int slot)
    {
        mc.player.connection.sendPacket(
                new CPacketAnimation(InventoryUtil.getHand(slot)));
    }

    protected double getY(Entity entity, double min, double max, boolean add) {
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
             off = (add ? ++off : --off))
        {
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
            if ( !state.getMaterial().blocksMovement()
                    || state.getBlock() == Blocks.AIR) {
                if (air) {
                    if (add) {
                        return pos.getY();
                    } else {
                        return  last.getY() ;
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

    protected double getY(Entity entity, int mode) {

        if (mode ==1) {
            double y = entity.posY -256;
            return y;
        }
        double d = getY(entity,1,5, true);
        if (Double.isNaN(d)) {
            d = getY(entity, -2,-5, false);
            if (Double.isNaN(d)) {

                    return getY(entity,1);

            }
        }

        return d;
    }
    public int findAntiWeakness (){
        int oldSlot =-1;
        for (int i = 0; i < 45; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemSword) {
                    oldSlot = i;
                    break;
                } else if (stack.getItem() instanceof ItemTool) {
                    oldSlot = i;
                    break;
                }
            }
        }
        return oldSlot;
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof SPacketExplosion){
            SPacketExplosion packet = (SPacketExplosion) event.packet;
         mc.player.motionY = packet.getMotionY();
            mc.player.motionY -= 1;
            scaleTimer.reset();

        }

        if (event.packet instanceof SPacketEntityVelocity){
            SPacketEntityVelocity packet = (SPacketEntityVelocity) event.packet;
            EntityPlayerSP playerSP = mc.player;
            if (playerSP != null
                    && packet.getEntityID() == playerSP.getEntityId())
            {
                mc.player.motionY =packet.getMotionY() / 8000.0;
                mc.player.motionY -= 1;
                scaleTimer.reset();
            }
        }
        if (event.packet instanceof SPacketSpawnObject){
            SPacketSpawnObject packet = (SPacketSpawnObject) event.packet;
            if ( packet.getType() != 51
                    || mc.world == null
                    || !isEnabled()
                    || ( mc.player.isPotionActive(MobEffects.WEAKNESS) && (!mc.player.isPotionActive(MobEffects.STRENGTH) || Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() < 1))
                    || mc.world.getBlockState(PositionUtil.getPosition(
                    RotationUtil.getRotationPlayer()).up(2))
                    .getMaterial()
                    .blocksMovement())
            {
                return;
            }
            EntityPlayerSP player = mc.player;
            if (player != null)
            {
                BlockPos pos = PositionUtil.getPosition(player);
                if (!mc.world.getBlockState(pos).getMaterial().isReplaceable())
                {
                    return;
                }

                EntityEnderCrystal crystal = new EntityEnderCrystal(mc.world,
                        packet.getX(),
                        packet.getY(),
                        packet.getZ());
                if (crystal.getEntityBoundingBox()
                        .intersects(new AxisAlignedBB(pos)))
                {
                    float damage = DamageUtil.calculate(crystal);
                    if (AlwaysshouldPop(damage,500))
                    {
                       attack(packet.getEntityID());
                    }
                }
            }
        }
        }
    public boolean isInsideBlock() {
        double x = mc.player.posX;
        double y = mc.player.posY + 0.20;
        double z = mc.player.posZ;
        return mc.world.getBlockState(new BlockPos(x, y, z)).getMaterial().blocksMovement() || !mc.player.collidedVertically;
    }

    /**
     * Produces a {@link CPacketUseEntity} for the given id.
     *
     * @param id the id the packet should attack.
     * @return a packet that will attack the entity when send.
     */
    @SuppressWarnings("ConstantConditions")
    public static CPacketUseEntity attackPacket(int id)
    {
        CPacketUseEntity packet = new CPacketUseEntity();
        ((AccessorCPacketUseEntity) packet).setId(id);
        ((AccessorCPacketUseEntity) packet).setAction(CPacketUseEntity.Action.ATTACK);

        return packet;
    }
    public static void attack(int id)
    {
        mc.player.connection.sendPacket(
                attackPacket(id));
        mc.player.connection.sendPacket(
                new CPacketAnimation(EnumHand.MAIN_HAND));
    }
    protected boolean singlePlayerCheck(BlockPos pos)
    {
        if (mc.isSingleplayer())
        {
            @SuppressWarnings("ConstantConditions")
            EntityPlayer player = mc.getIntegratedServer()
                    .getPlayerList()
                    .getPlayerByUUID(mc.player.getUniqueID());
            //noinspection ConstantConditions
            if (player == null)
            {
                this.disable();
                return true;
            }

            player.getEntityWorld().setBlockState(pos, echest.getValue()
                    ? Blocks.ENDER_CHEST.getDefaultState()
                    : Blocks.OBSIDIAN.getDefaultState());

            mc.world.setBlockState(pos, echest.getValue()
                    ? Blocks.ENDER_CHEST.getDefaultState()
                    : Blocks.OBSIDIAN.getDefaultState());

            return true;
        }

        return false;
    }
    protected BlockPos getPlayerPos() {
        return  Math.abs(mc.player.motionY) > 0.1
                ? new BlockPos(mc.player)
                : PositionUtil.getPosition(mc.player);
    }
    public boolean shouldPop(float damage, int popTime)
    {
        return damage < EntityUtil.getHealth(mc.player) + 1.0;
    }
    public boolean AlwaysshouldPop(float damage, int popTime)
    {
        return shouldPop(damage, popTime)
                || mc.player.getHeldItemOffhand().getItem()
                == Items.TOTEM_OF_UNDYING;
    }
    protected double applyScale(double value) {

        if (value < mc.player.posY) {
            value -= (motionY * scaleFactor.getValue());
        } else {
            value += (motionY * scaleFactor.getValue());
        }


        return  Math.floor(value);
    }

    public static void doY(double y, boolean onGround)
    {
        doY(mc.player, y, onGround);
    }

    public static void doY(Entity entity, double y, boolean onGround)
    {
        doPosition(entity.posX, y, entity.posZ, onGround);
    }

    public static void doPosition(double x,
                                  double y,
                                  double z,
                                  boolean onGround)
    {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, onGround));
    }
    public static void place(BlockPos on,
                             EnumFacing facing,
                             int slot,
                             float x,
                             float y,
                             float z)
    {
        place(on, facing, InventoryUtil.getHand(slot), x, y, z);
    }

    public static void place(BlockPos on,
                             EnumFacing facing,
                             EnumHand hand,
                             float x,
                             float y,
                             float z)
    {
        mc.player.connection.sendPacket(
                new CPacketPlayerTryUseItemOnBlock(on, facing, hand, x, y, z));
    }
}

