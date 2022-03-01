package dev.cuican.staypro.module.modules.combat;


import dev.cuican.staypro.client.FriendManager;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.concurrent.event.Priority;
import dev.cuican.staypro.concurrent.utils.Timer;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.event.events.render.RenderModelEvent;
import dev.cuican.staypro.mixin.accessor.AccessorCPacketUseEntity;
import dev.cuican.staypro.mixin.accessor.AccessorMinecraft;
import dev.cuican.staypro.mixin.accessor.AccessorTimer;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.*;
import dev.cuican.staypro.utils.block.BlockInteractionHelper;
import dev.cuican.staypro.utils.graphics.StayTessellator;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import dev.cuican.staypro.utils.nnLinear.animations.BlockEasingRender;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.Explosion;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by zenhao on 07/06/2021.
 * Updated by zenhao on 21/08/2021.
 */
@ModuleInfo(name = "ZetaCrystal", category = Category.COMBAT)
public class ZetaCrystal extends Module {
    public static CopyOnWriteArrayList<CPacketUseEntity> packetList = new CopyOnWriteArrayList<>();
    public static ZetaCrystal INSTANCE = new ZetaCrystal();
    public static Vec3d lastLookAt = Vec3d.ZERO;
    public static Entity renderEnt;
    public Setting<String> p = setting("Page","GENERAL",listOf( "GENERAL", "COMBAT", "DEV", "RENDER"));
    //Page GENERAL
    public Setting<Boolean> place = setting("Place", true).whenAtMode(p, "GENERAL");
    public  Setting<Boolean> multiplace = setting("MultiPlace", false).whenAtMode(p, "GENERAL");
    public Setting<Integer> PlaceSpeed = setting("PlaceSpeed", 36, 1, 45).whenTrue(place).whenAtMode(p, "GENERAL");
    public  Setting<Boolean> explode = setting("Explode", true).whenAtMode(p, "GENERAL");
    public Setting<Integer> hitdelay = setting("HitDelay", 30, 0, 500).whenTrue(explode).whenAtMode(p, "GENERAL");
    public  Setting<Boolean> antiWeakness = setting("AntiWeakness", false).whenTrue(explode).whenAtMode(p, "GENERAL");
    public  Setting<Boolean> packetAntiWeak = setting("PacketAntiWeakness", false).whenTrue(antiWeakness).whenAtMode(p, "GENERAL");
    public  Setting<Boolean> wall = setting("Wall", true).whenAtMode(p, "GENERAL");
    public  Setting<Boolean> wallAI = setting("WallAI", true).whenTrue(wall).whenAtMode(p, "GENERAL");
    public Setting<Integer> enemyRange = setting("EnemyRange", 7, 1, 10).whenAtMode(p, "GENERAL");
    public  Setting<Double> placeRange = setting("PlaceRange", 5.5D, 0D, 6D).whenTrue(place).whenAtMode(p, "GENERAL");
    public Setting<Double> breakRange = setting("BreakRange", 5.5, 0, 6).whenTrue(explode).whenAtMode(p, "GENERAL");
    public Setting<Integer> breakMinDmg = setting("BreakMinDmg", 2, 0, 36).whenTrue(explode).whenAtMode(p, "GENERAL");
    public Setting<Double> breakWallRange = setting("BreakWallRange", 3, 0.1, 4).whenTrue(wall).whenAtMode(p, "GENERAL");
    public Setting<Double> placeWallRange = setting("PlaceWallRange", 3, 0.1, 4).whenTrue(wall).whenAtMode(p, "GENERAL");
    public Setting<Integer> minDamage = setting("MinDmg", 4, 0, 36).whenAtMode(p, "GENERAL");
    public Setting<Integer> MaxselfDMG = setting("MaxSelfDmg", 12, 0, 36).whenAtMode(p, "GENERAL");
    //Page COMBAT
    public Setting<String> switchmode = setting("SwitchMode","Off",listOf( "AutoSwitch", "GhostHand", "Off")).whenAtMode(p,"COMBAT");
    public  Setting<Boolean> rotate = setting("Rotate", true).whenAtMode(p,"COMBAT");
    public  Setting<Boolean> endcrystal = setting("1.13Place", false).whenAtMode(p,"COMBAT");
    public  Setting<Boolean> speedDebug = setting("SpeedDebug", false).whenAtMode(p,"COMBAT");
    public  Setting<Boolean> FacePlace = setting("FacePlace", true).whenAtMode(p,"COMBAT");
    public Setting<Integer> BlastHealth = setting("BlastHealth", 10, 0, 20).whenAtMode(p,"COMBAT");
    public  Setting<Boolean> ArmorCheck = setting("ArmorFucker", true).whenAtMode(p,"COMBAT");
    public Setting<Integer> ArmorRate = setting("Armor%", 15, 0, 100).whenTrue(ArmorCheck).whenAtMode(p,"COMBAT");
    public  Setting<Boolean> PacketExplode = setting("PacketExplode", true).whenAtMode(p,"COMBAT");
    public Setting<Integer> PacketExplodeDelay = setting("PacketExplodeDelay", 45, 0, 500).whenTrue(PacketExplode).whenAtMode(p,"COMBAT");
    public  Setting<Boolean> ClientSide = setting("ClientSide", false).whenAtMode(p,"COMBAT");
    public  Setting<Boolean> PredictHit = setting("PredictHit", false).whenAtMode(p,"COMBAT");
    public Setting<Integer> PredictHitFactor = setting("PredictHitFactor", 2, 1, 20).whenTrue(PredictHit).whenAtMode(p,"COMBAT");
    public  Setting<Boolean> MotionPredict = setting("MotionPredict", true).whenAtMode(p,"COMBAT");
    //Page DEV
    public Setting<Boolean> AutoMineHole = setting("AutoHoleMining", false).whenAtMode(p, "DEV");
    public Setting<Boolean> packetOptimize = setting("PacketOptimize", true).whenAtMode(p, "DEV");
    //Page RENDER
    public Setting<String> mode = setting("Mode","FULL",listOf("SOLID", "SOLIDFLAT", "FULL", "OUTLINE")).whenAtMode(p, "RENDER");
    public Setting<Boolean> renderDamage = setting("RenderDamage", true).whenAtMode(p, "RENDER");
    public Setting<Integer> red = setting("Red", 255, 0, 255).whenAtMode(p, "RENDER");
    public Setting<Integer> green = setting("Green", 255, 0, 255).whenAtMode(p, "RENDER");
    public Setting<Integer> blue = setting("Blue", 255, 0, 255).whenAtMode(p, "RENDER");
    public Setting<Integer> alpha = setting("Alpha", 60, 0, 255).whenAtMode(p, "RENDER");
    public Setting<Boolean> rainbow = setting("Rainbow", true).whenAtMode(p, "RENDER");
    public Setting<Integer> RGBSpeed = setting("RGBSpeed", 1, 0, 255).whenTrue(rainbow).whenAtMode(p, "RENDER");
    public Setting<Float> Saturation = setting("Saturation", 0.3f, 0, 1).whenTrue(rainbow).whenAtMode(p, "RENDER");
    public Setting<Float> Brightness = setting("Brightness", 1f, 0, 1).whenTrue(rainbow).whenAtMode(p, "RENDER");
    public BlockEasingRender blockRenderSmooth = new BlockEasingRender(new BlockPos(0, 0, 0), 400L,400L);
    public FadeUtils fadeBlockSize = new FadeUtils(350L);
    public Timer PacketExplodeTimer = new Timer();
    public Timer HoleMiningTimer = new Timer();
    public Timer ExplodeTimer = new Timer();
    public Timer SwitchTimer = new Timer();
    public Timer PlaceTimer = new Timer();
    public Timer CalcTimer = new Timer();
    public EntityEnderCrystal lastCrystal;
    public Vec3d PredictionTarget;
    public BlockPos OffRenderPos;
    public BlockPos render;
    public BlockPos webPos;
    public boolean ShouldOffFadeRender = false;
    public boolean ShouldInfoLastBreak = false;
    public boolean ShouldDisableRender = true;
    public boolean ShouldOffFadeReset = false;
    public boolean ShouldShadeRender = false;
    public boolean switchCooldown = false;
    public boolean afterAttacking = false;
    public boolean canPredictHit = false;
    public boolean isAttacking = false;
    public boolean ShouldStop = false;
    public boolean isActive = false;
    public boolean switched = false;
    public boolean canMine = false;
    public long infoBreakTime = 0L;
    public long lastBreakTime = 0L;
    public int lastEntityID = -1;
    public int placements = 0;
    public int StuckTimes = 0;
    public int crystals = 0;
    public int newSlot = -1;
    public int oldSlot = -1;
    public int CSlot = -1;

    public static EnumFacing enumFacing(BlockPos blockPos) {
        try {
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
        } catch (Exception ignored) {
        }
        return EnumFacing.UP;
    }

    public static boolean CanSeeBlock(BlockPos blockPos) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), false, true, false) != null;
    }


    float renderPitch;


    Boolean rotating = false;
    @Listener(priority = Priority.HIGHEST)
    public void renderModelRotation(RenderModelEvent event) {
        if (!rotate.getValue()) return;
        if (rotating) {
            event.rotating = true;
            event.pitch = renderPitch;
        }
    }
    public static double getVdistance(BlockPos blockPos, double n, double n2, double n3) {
        double n4 = blockPos.getX() - n;
        double n5 = blockPos.getY() - n2;
        double n6 = blockPos.getZ() - n3;
        return Math.sqrt(n4 * n4 + n5 * n5 + n6 * n6);
    }

    public static double getRange(Vec3d a, double x, double y, double z) {
        double xl = a.x - x;
        double yl = a.y - y;
        double zl = a.z - z;
        return Math.sqrt(xl * xl + yl * yl + zl * zl);
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (Exception ignored) {
            }
            float f = MathHelper.clamp((float) k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    public static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }

    public static Vec3d PredictionHandler(EntityPlayer target, boolean predict) {
        double partialTick = ((AccessorTimer)((AccessorMinecraft) Minecraft.getMinecraft()).aqGetTimer()).aqGetTickLength() / 1000.0f;
        double posX = target.posX + (predict ? ((target.posX - target.prevPosX) / partialTick) * 0.6 : 0);
        double posY = target.getEntityBoundingBox().minY + (predict ? ((target.getEntityBoundingBox().minY - target.prevPosY) / partialTick) * 0.6 : 0) + target.getEyeHeight() - 0.15;
        double posZ = target.posZ + (predict ? ((target.posZ - target.prevPosZ) / partialTick) * 0.6 : 0);
        return new Vec3d(posX, posY, posZ);
    }

//    @Listener
//    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
//        if (fullNullCheck()) {
//            return;
//        }
//        if (PredictHit.getValue()) {
//            toggle();
//        }
//    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (fullNullCheck()) {
            return;
        }
        if (packetOptimize.getValue()) {
            if (event.getPacket() instanceof CPacketUseEntity) {
                if (packetList.size() > 40) {
                    event.isCancelled();
                    packetList.clear();
                }
            }
        }
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (fullNullCheck()) {
            return;
        }

        if (event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            if (PredictHit.getValue()) {
                for (Entity e : new ArrayList<>(mc.world.loadedEntityList)) {
                    if (e instanceof EntityItem || e instanceof EntityArrow || e instanceof EntityEnderPearl || e instanceof EntitySnowball || e instanceof EntityEgg) {
                        if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6) {
                            lastEntityID = -1;
                            canPredictHit = false;
                            event.isCancelled();
                            return;
                        }
                    }
                }
            }
        }
        if ( event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet5 = (SPacketSoundEffect) event.getPacket();
            if (packet5.getSound().equals(SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW) || packet5.getSound().equals(SoundEvents.ENTITY_ITEM_BREAK)) {
                canPredictHit = false;
            }
            if (packet5.getSound().equals(SoundEvents.ITEM_TOTEM_USE)) {
                for (Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                    if (entity instanceof EntityPlayer) {
                        renderEnt = entity;
                    }
                }
            }
            if (packet5.getCategory() == SoundCategory.BLOCKS && packet5.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE && render != null) {
                ShouldInfoLastBreak = true;
                for (Entity e : new ArrayList<>(mc.world.loadedEntityList)) {
                    if (e instanceof EntityEnderCrystal) {
                        if (e.getDistance(packet5.getX(), packet5.getY(), packet5.getZ()) <= 6.0f) {
                            e.setDead();
                            crystals++;
                        }
                    }
                }
            }
        }
        if (event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51) {
                lastEntityID = packet.getEntityID();
                if (PacketExplode.getValue() && PacketExplodeTimer.passed(PacketExplodeDelay.getValue()) && explode.getValue() && lastCrystal != null) {
                    if (wall.getValue() && mc.player.getDistance(lastCrystal) > breakWallRange.getValue() && CanSeeBlock(new BlockPos(lastCrystal.getPositionVector()))) {
                        return;
                    }
                    PacketExplode(packet.getEntityID());
                    PacketExplodeTimer.reset();
                }
            }
        }
    }

    @Override
    public void onTick() {
        if (fullNullCheck()) {
            return;
        }
        if (CalcTimer.passed(1000)) {
            CalcTimer.reset();
            if (speedDebug.getValue()) {
                ChatUtil.NoSpam.sendRawChatMessage("CrystalSpeed: " + crystals + " Crystals/s");
            }
            crystals = 0;
        }
        oldSlot = mc.player.inventory.currentItem;
        newSlot = mc.player.inventory.currentItem;
        CSlot = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
        Update();
    }

    public void Update() {
        if (fullNullCheck()) {
            return;
        }
        explode();
        place();
    }

    public void explode() {
        EntityEnderCrystal crystal = mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal &&
                canHitCrystal((EntityEnderCrystal) e)).map(e -> (EntityEnderCrystal) e).min(Comparator.comparing(e -> mc.player.getDistance(e))).orElse(null);
        if (mc.player != null && crystal != null && renderEnt != null) {
            if (explode.getValue() && mc.player.getDistance(crystal) <= breakRange.getValue()) {
                lastCrystal = crystal;
                if (!mc.player.canEntityBeSeen(crystal)) {
                    afterAttacking = false;
                    StuckTimes++;
                }

                if (mc.player.canEntityBeSeen(crystal) || (mc.player.getDistance(crystal) < breakWallRange.getValue() && wall.getValue())) {
                    if (StuckTimes > 0) {
                        StuckTimes = 0;
                    }
                    ExplodeCrystal();
                    afterAttacking = true;
                }
            }


            if(ClientSide.getValue()){
                for (Object o : new ArrayList(mc.world.loadedEntityList)) {
                    Entity e = (Entity) o;
                    if (e instanceof EntityEnderCrystal && e.getDistance(e.posX, e.posY, e.posZ) <= 6.0D) {
                        e.setDead();
                    }
                }

                mc.world.removeAllEntities();
                mc.world.getLoadedEntityList();
            }

            if (multiplace.getValue()) {
                if (placements >= 3) {
                    placements = 0;
                    afterAttacking = true;
                }
            }
        }
    }

    Boolean togglePitch;
    Boolean shouldSpoofPacket;
    public void place() {
        try {
            int inft = -1;
            CSlot = InventoryUtil.getItemHotbar(Items.END_CRYSTAL);
            int crystalSlot = (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) ? mc.player.inventory.currentItem : -1;
            if (crystalSlot == -1) {
                for (int l = 0; l < 9; ++l) {
                    if (mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                        crystalSlot = l;
                        break;
                    }
                }
            }
            boolean offhand = false;
            if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
                offhand = true;
            } else if (crystalSlot == -1) {
                return;
            }
            CrystalTarget crystalTarget = Calc();
            renderEnt = crystalTarget.target;
            render = crystalTarget.blockPos;
            if(renderEnt==null)   blockRenderSmooth.end();
            if (crystalTarget.dmg == 0.5) {
                ShouldShadeRender = true;
                render = null;
                renderEnt = null;
                blockRenderSmooth.end();
                return;
            } else if (ShouldShadeRender && render != null) {
                blockRenderSmooth.begin();
                fadeBlockSize.reset();
                ShouldShadeRender = false;
            }


//            if (!ModuleManager.getModuleByName("AutoGG").isDisabled() && renderEnt != null) {
//                AutoGG.INSTANCE.addTargetedPlayer(renderEnt.getName());
//            }

            if (place.getValue() && render != null) {
                lastLookAt = new Vec3d(render).add(0.5, 0, 0.5);
                float[] legitRotations = BlockInteractionHelper.getLegitRotations(new Vec3d(lastLookAt.x, enumFacing(render).equals(EnumFacing.UP) ? (lastLookAt.y + 1) : (lastLookAt.y - 1), lastLookAt.z));
                if (rotate.getValue()) look.lookAt(lastLookAt);

                if (!offhand && mc.player.inventory.currentItem != crystalSlot&&switchmode.getValue() .equals("AutoSwitch")) {
                    if (mc.player.getHeldItemMainhand().getItem() instanceof ItemAppleGold && mc.player.isHandActive()) {
                        isActive = false;
                        return;
                    }
                    isActive = true;
                    mc.player.inventory.currentItem = crystalSlot;
                    switchCooldown = true;
                    return;
                }
                if (switchCooldown) {
                    switchCooldown = false;
                    return;
                }
                EnumFacing nmsl = enumFacing(render);
                if (switchmode.getValue().equals("GhostHand")&& mc.player.inventory.currentItem != crystalSlot&&InventoryUtil.getItemHotbar(Items.END_CRYSTAL) != -1) {
                    inft = mc.player.inventory.currentItem;
                    mc.player.inventory.currentItem = crystalSlot;
                    mc.playerController.updateController();
                }
                if (mc.getConnection() != null) {
                    for (int a = 0; a < 3; a++) {
                        if (hasDelayRun(PlaceSpeed.getValue())) {
                            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(render, nmsl, mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                            blockRenderSmooth.updatePos(render);
                            placements++;
                            if (PredictHit.getValue() && renderEnt != null) {
                                try {
                                    if (renderEnt.isDead || !canPredictHit || !mc.player.canEntityBeSeen(lastCrystal)) {
                                        PlaceTimer.reset();
                                        return;
                                    }
                                    if (wall.getValue() && mc.player.getDistance(lastCrystal) > breakWallRange.getValue() && CanSeeBlock(new BlockPos(lastCrystal.getPositionVector()))) {
                                        return;
                                    }
                                    if (mc.player.getHealth() + mc.player.getAbsorptionAmount() > MaxselfDMG.getValue() && lastEntityID != -1 && lastCrystal != null && canPredictHit) {
                                        for (int i = 0; i < PredictHitFactor.getValue(); i++) {
                                            PacketExplode(lastEntityID + i + 2);
                                        }
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                            PlaceTimer.reset();
                        }
                    }
                }
                if (inft!=-1&&switchmode.getValue().equals("GhostHand")) {
                    mc.player.inventory.currentItem = inft;
                    mc.playerController.updateController();
                }
            }
            if (AutoMineHole.getValue()) {
                BlockPos minePos = null;
                BlockPos blockPos = new BlockPos(renderEnt.posX, renderEnt.posY, renderEnt.posZ);
                for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                    IBlockState touchingState = mc.world.getBlockState(blockPos.offset(facing));
                    if (touchingState.getBlock() == Blocks.OBSIDIAN) {
                        canMine = true;
                        minePos = blockPos.offset(facing);
                    }
                }
                if (EntityUtil.isInHole(renderEnt) && canMine && minePos != null && mc.getConnection() != null) {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    if (HoleMiningTimer.passed(hitdelay.getValue())) {
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, minePos, EnumFacing.DOWN));
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, minePos, EnumFacing.DOWN));
                        HoleMiningTimer.reset();
                    }
                }
            }

        } catch (Exception ignored) {
        }
    }

    public void switchIt(int slot) {
        if (fullNullCheck()) {
            return;
        }
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    public CrystalTarget Calc() {
        //Calc
        List<Entity> entities = getEntities();
        double damage = 0.5;
        newSlot = mc.player.inventory.currentItem;
        BlockPos setToAir = null;
        IBlockState state = null;
        List<BlockPos> default_blocks;
        if (wall.getValue() && wallAI.getValue()) {
            double TempRange = placeRange.getValue();
            double temp2 = TempRange - (StuckTimes * 0.5);
            if (StuckTimes > 0) {
                TempRange = placeRange.getValue();
                if (temp2 > placeWallRange.getValue()) {
                    TempRange = temp2;
                } else if (placeWallRange.getValue() < placeRange.getValue()) {
                    TempRange = 3.0;
                }
            }
            default_blocks = rendertions(TempRange);
        } else {
            default_blocks = rendertions(placeRange.getValue());
        }
        for (Entity entity2 : new ArrayList<>(entities)) {
            if (entity2 != mc.player) {
                if (entity2 instanceof EntityPlayer) {
                    PredictionTarget = entity2 instanceof EntityPlayer ?
                            PredictionHandler((EntityPlayer) entity2, MotionPredict.getValue()) :
                            new Vec3d(entity2.posX, entity2.posY, entity2.posZ);
                    BlockPos playerPos = new BlockPos(entity2.getPositionVector());
                    Block web = mc.world.getBlockState(playerPos).getBlock();
                    if (web == Blocks.WEB) {
                        setToAir = playerPos;
                        state = mc.world.getBlockState(playerPos);
                        mc.world.setBlockToAir(playerPos);
                    }
                    if (((EntityLivingBase) entity2).getHealth() <= 0.0f) {
                        continue;
                    }
                    BlockPos vec = new BlockPos(entity2.posX, entity2.posY, entity2.posZ);
                    Vec3d doubleTargetPos = new Vec3d(vec);
                    List<BlockPos> legBlocks = findLegBlocks(doubleTargetPos);
                    canPredictHit = (!PredictHit.getValue() || !((EntityPlayer) entity2).getHeldItemMainhand().getItem().equals(Items.EXPERIENCE_BOTTLE)) && !((EntityPlayer) entity2).getHeldItemOffhand().getItem().equals(Items.EXPERIENCE_BOTTLE) || ModuleManager.getModuleByName("AutoExp").isDisabled();
                    legBlocks.addAll(default_blocks);
                    for (BlockPos blockPos : new ArrayList<>(legBlocks)) {
                        double d;
                        d = calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, entity2, PredictionTarget);
                        if(blockPos.equals(vec)) continue;
                        if (d < damage) continue;
                        if (entity2.getDistanceSq(blockPos) >= enemyRange.getValue() * enemyRange.getValue()) continue;
                        if (mc.player.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) > placeRange.getValue()) continue;
                        if (d < (FacePlace.getValue() ? (canFacePlace((EntityLivingBase) entity2) ? 1 : minDamage.getValue()) : minDamage.getValue()))
                            continue;
                        float healthTarget = ((EntityLivingBase) entity2).getHealth() + ((EntityLivingBase) entity2).getAbsorptionAmount();
                        float healthSelf = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                        double self = calculateDamage(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, mc.player);
                        if (self > d && d < healthTarget) continue;
                        if (self - 0.5 > healthSelf) continue;
                        if (self > MaxselfDMG.getValue()) continue;
                        if (!wall.getValue()) {
                            if (placeWallRange.getValue() > 0)
                                if (CanSeeBlock(blockPos))
                                    if (getVdistance(blockPos, mc.player.posX, mc.player.posY, mc.player.posZ) > placeWallRange.getValue())
                                        continue;
                        }
                        damage = d;
                        render = blockPos;
                        renderEnt = entity2;
                    }
                    if (setToAir != null) {
                        mc.world.setBlockState(setToAir, state);
                        webPos = render;
                    }
                }
                if (renderEnt != null) {
                    break;
                }
            }
        }
        return new CrystalTarget(render, renderEnt, damage);
    }

    public boolean hasDelayRun(double placeSpeed) {
        return PlaceTimer.passed((int) (1000 / placeSpeed));
    }

    public List<Entity> getEntities() {
        List<Entity> entities = mc.world.playerEntities.stream()
                .filter(entityPlayer -> !FriendManager.isFriend(entityPlayer.getName()))
                .filter(entity -> mc.player.getDistance(entity) < enemyRange.getValue())
                .collect(Collectors.toList());
        for (Entity ite2 : new ArrayList<>(entities)) {
            if (mc.player.getDistance(ite2) > enemyRange.getValue()) entities.remove(ite2);
            if (ite2 == mc.player) entities.remove(ite2);
        }
        try {
            entities.sort(Comparator.comparingDouble(entity -> entity.getDistance(mc.player)));
        } catch (Exception ignored) {
        }
        return entities;
    }

    public void ExplodeCrystal() {
        oldSlot = mc.player.inventory.currentItem;
        EntityEnderCrystal crystal = mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal &&
                canHitCrystal((EntityEnderCrystal) e)).map(e -> (EntityEnderCrystal) e).min(Comparator.comparing(e -> mc.player.getDistance(e))).orElse(null);
        if (crystal != null) {
            lastLookAt = crystal.getPositionVector();
            if (rotate.getValue()) {
                look.lookAt(new Vec3d(lastLookAt.x, lastLookAt.y, lastLookAt.z));
            }
            if (ExplodeTimer.passed(hitdelay.getValue()) && mc.getConnection() != null) {
                if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS) && (!mc.player.isPotionActive(MobEffects.STRENGTH) || Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() < 1)) {
                    if (!isAttacking) {
                        oldSlot = mc.player.inventory.currentItem;
                        isAttacking = true;
                    }

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
                    if (oldSlot != -1) {
                        if (packetAntiWeak.getValue()) {
                            switchIt(oldSlot);
                            switched = true;
                        } else {
                            mc.player.inventory.currentItem = oldSlot;
                            switchCooldown = true;
                        }
                    }
                }
                PacketExplode(crystal.getEntityId());
                mc.player.swingArm(mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                mc.player.resetCooldown();
                ExplodeTimer.reset();
            }
            if (switched) {
                switchIt(newSlot);
            }
            if (lastBreakTime == 0L) {
                lastBreakTime = System.currentTimeMillis();
                ShouldInfoLastBreak = false;
            }
        }
    }

    public void PacketExplode(int i) {
        if (lastCrystal != null) {
            try {
                if (mc.player.getDistance(lastCrystal) > breakRange.getValue() || !canHitCrystal(lastCrystal)) return;
                CPacketUseEntity wdnmd = new CPacketUseEntity();
                setEntityId(wdnmd,i);
                setAction(wdnmd,CPacketUseEntity.Action.ATTACK);
                mc.player.connection.sendPacket(wdnmd);
                if (packetOptimize.getValue()) {
                    packetList.add(wdnmd);
                }
            } catch (Exception ignored) {
            }
        }
    }
    public static void setEntityId(CPacketUseEntity packet, int entityId) {
        ((AccessorCPacketUseEntity) packet).setId(entityId);
    }
    public static void setAction(CPacketUseEntity packet, CPacketUseEntity.Action action) {
        ((AccessorCPacketUseEntity) packet).setAction(action);
    }
    public boolean canHitCrystal(EntityEnderCrystal crystal) {
        if (mc.player.getDistance(crystal) > breakRange.getValue()) return false;
        float selfDamage = calculateDamage(crystal.posX, crystal.posY, crystal.posZ, mc.player);
        float healthSelf = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if (selfDamage >= healthSelf) return false;
        List<EntityPlayer> entities = mc.world.playerEntities.stream()
                .filter(e -> mc.player.getDistance(e) <= enemyRange.getValue())
                .filter(e -> mc.player != e)
                .filter(e -> !FriendManager.isFriend(e.getName()))
                .sorted(Comparator.comparing(e -> mc.player.getDistance(e)))
                .collect(Collectors.toList());
        for (EntityPlayer player : new ArrayList<>(entities)) {
            if (mc.player.isDead || healthSelf <= 0.0f) continue;
            double minDamage = breakMinDmg.getValue();
            if (canFacePlace(player)) {
                minDamage = 1;
            }
            double target = calculateDamage(crystal.posX, crystal.posY, crystal.posZ, player);
            if (target > player.getHealth() + player.getAbsorptionAmount() && selfDamage < healthSelf) {
                return true;
            }
            if (target < minDamage) continue;
            if (selfDamage > target) continue;
            return true;
        }
        return false;
    }
    public boolean canFacePlace(EntityLivingBase target) {
        float healthTarget = target.getHealth() + target.getAbsorptionAmount();
        if (healthTarget <= BlastHealth.getValue()) {
            return true;
        } else if (ArmorCheck.getValue()) {
            for (ItemStack itemStack : target.getArmorInventoryList()) {
                if (itemStack.isEmpty()) {
                    continue;
                }
                float dmg = ((float) itemStack.getMaxDamage() - (float) itemStack.getItemDamage()) / (float) itemStack.getMaxDamage();
                if (dmg <= ArmorRate.getValue() / 100f) {
                    return true;
                }
            }
        }
        return false;
    }
    public List<BlockPos> rendertions(double range) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(CrystalUtil.getSphere(getPlayerPos(), range, range,
                false,
                true, 0).stream()
                .filter(v -> canPlaceCrystal(v, endcrystal.getValue())).collect(Collectors.toList()));
        return positions;
    }
    private List<BlockPos> findLegBlocks(Vec3d targetPos) {
        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(BlockInteractionHelper.getLegVec(targetPos.add(0, 0, 0))
                .stream()
                .filter(v -> canPlaceCrystal(v, endcrystal.getValue())).collect(Collectors.toList()));
        return positions;
    }
    public Vec3d getPlayerPos() {
        return new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
    }
    public boolean canPlaceCrystal(BlockPos blockPos, boolean newPlace) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        if (mc.world.getBlockState(boost).getBlock() == Blocks.WATER
                || mc.world.getBlockState(boost).getBlock() == Blocks.WATERLILY
                || mc.world.getBlockState(boost).getBlock() == Blocks.FLOWING_WATER
                || mc.world.getBlockState(boost).getBlock() == Blocks.MAGMA
                || mc.world.getBlockState(boost).getBlock() == Blocks.LAVA
                || mc.world.getBlockState(boost).getBlock() == Blocks.FLOWING_LAVA
        ) return false;
        if (mc.world.getBlockState(boost2).getBlock() == Blocks.WATER
                || mc.world.getBlockState(boost2).getBlock() == Blocks.WATERLILY
                || mc.world.getBlockState(boost2).getBlock() == Blocks.FLOWING_WATER
                || mc.world.getBlockState(boost2).getBlock() == Blocks.MAGMA
                || mc.world.getBlockState(boost2).getBlock() == Blocks.LAVA
                || mc.world.getBlockState(boost2).getBlock() == Blocks.FLOWING_LAVA
        ) return false;
        if (newPlace) {
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR) {
                return false;
            }
            for (Entity entity : new ArrayList<>(mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)))) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    return false;
                }
            }
            for (Entity entity : new ArrayList<>(mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)))) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    return false;
                }
            }
            webcalc();
        } else {
            if (mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR || mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                return false;
            }
            for (Entity entity : new ArrayList<>(mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)))) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    return false;
                }
            }
            for (Entity entity : new ArrayList<>(mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)))) {
                if (!(entity instanceof EntityEnderCrystal)) {
                    return false;
                }
            }
            webcalc();
        }
        if (multiplace.getValue()) {
            return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                    || mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
                    && mc.world.getBlockState(boost).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(boost2).getBlock() == Blocks.AIR
                    && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty()
                    && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
        }
        if (afterAttacking) {
            for (Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                if (!(entity instanceof EntityEnderCrystal)) continue;
                EntityEnderCrystal entityEnderCrystal = (EntityEnderCrystal) entity;
                double d2 = lastCrystal != null ? lastCrystal.getDistance((double) blockPos.getX() + 0.5, (blockPos.getY() + 1), (double) blockPos.getZ() + 0.5) : 10000.0;
                if (!(d2 > 6.0) || !(getRange(entityEnderCrystal.getPositionVector(), (double) blockPos.getX() + 0.5, 0, (double) blockPos.getZ() + 0.5) < 2.0)
                        || !(Math.abs(entityEnderCrystal.posY - (double) (blockPos.getY() + 1)) < 2.0)) continue;
                return false;
            }
            return !(!mc.world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(boost)).isEmpty()
                    || !mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(boost)).isEmpty()
                    || !mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(boost2)).isEmpty()
                    || !mc.world.getEntitiesWithinAABB(EntityArrow.class, new AxisAlignedBB(boost)).isEmpty());
        } else {
            return true;
        }
    }

    public void webcalc() {
        if (webPos != null) {
            if (mc.player.getDistanceSq(webPos) > MathUtil.square(breakRange.getValue())) {
                webPos = null;
            } else {
                for (Entity entity : new ArrayList<>(mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(webPos)))) {
                    if (entity instanceof EntityEnderCrystal) {
                        webPos = null;
                        return;
                    }
                }
            }
        }
    }


    @Listener
    public void onRenderWorld(RenderEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (render != null) {
            OffRenderPos = new BlockPos(render);
            ShouldOffFadeReset = true;
            if (ShouldOffFadeRender) {
                ShouldOffFadeRender = false;
                fadeBlockSize.reset();
            }
            GL11.glPushMatrix();
            Vec3d interpolateEntity = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
            AxisAlignedBB pos = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D).offset(blockRenderSmooth.getUpdate());
            pos = pos.grow(0.0020000000949949026).offset(-interpolateEntity.x, -interpolateEntity.y, -interpolateEntity.z);
            renderESP(pos, (float) fadeBlockSize.easeOutQuad());
            GL11.glPopMatrix();
            if (renderDamage.getValue()) {

                    GL11.glPushMatrix();
                    Vec3d renderr = blockRenderSmooth.getUpdate();
                    GlStateManager.shadeModel(GL11.GL_SMOOTH);
                    StayTessellator.glBillboardDistanceScaled((float) renderr.x + 0.5f, (float) renderr.y + 0.5f, (float) renderr.z + 0.5f, mc.player, 1);
                    float damage = calculateDamage(render.getX() + 0.5, render.getY() + 1, render.getZ() + 0.5, renderEnt);
                    String damageText = (Math.floor(damage) == damage ? damage : String.format("%.1f", damage)) + "";
                    GlStateManager.disableDepth();
                    GlStateManager.translate(-(mc.fontRenderer.getStringWidth(damageText) / 2d), 0, 0);
                    GlStateManager.scale(1, 1, 1);
                    FontUtils.Comfortaa.drawString("\u00a7e" + damageText, 1, 1, new Color(255, 255, 255).getRGB());
                GlStateManager.enableDepth();
                    GL11.glPopMatrix();

            }
        } else {
            if (ShouldOffFadeReset) {
                ShouldOffFadeReset = false;
                ShouldOffFadeRender = true;
                fadeBlockSize.reset();
            } else {
                if (fadeBlockSize.isEnd()) {
                    ShouldOffFadeRender = false;
                }
            }
            if (ShouldOffFadeRender) {
                if (OffRenderPos != null) {
                    Vec3d interpolateEntity = MathUtil.interpolateEntity(mc.player, mc.getRenderPartialTicks());
                    AxisAlignedBB pos = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D).offset(OffRenderPos);
                    pos = pos.grow(0.0020000000949949026).offset(-interpolateEntity.x, -interpolateEntity.y, -interpolateEntity.z);
                    renderESP(pos, (float) (1 - fadeBlockSize.easeOutQuad()));
                }
            }
        }
    }

    public void renderESP(AxisAlignedBB axisAlignedBB, float size) {
        int hsBtoRGB = Color.HSBtoRGB((new float[]{
                System.currentTimeMillis() % 11520L / 11520.0f * RGBSpeed.getValue()
        })[0], Saturation.getValue(), Brightness.getValue());
        int r = (hsBtoRGB >> 16 & 0xFF);
        int g = (hsBtoRGB >> 8 & 0xFF);
        int b = (hsBtoRGB & 0xFF);
        double centerX = axisAlignedBB.minX + ((axisAlignedBB.maxX - axisAlignedBB.minX) / 2);
        double centerY = axisAlignedBB.minY + ((axisAlignedBB.maxY - axisAlignedBB.minY) / 2);
        double centerZ = axisAlignedBB.minZ + ((axisAlignedBB.maxZ - axisAlignedBB.minZ) / 2);
        double fullX = (axisAlignedBB.maxX - centerX);
        double fullY = (axisAlignedBB.maxY - centerY);
        double fullZ = (axisAlignedBB.maxZ - centerZ);
        double progressValX = fullX * size;
        double progressValY = fullY * size;
        double progressValZ = fullZ * size;
        AxisAlignedBB axisAlignedBB1 = new AxisAlignedBB(centerX - progressValX, centerY - progressValY, centerZ - progressValZ, centerX + progressValX, centerY + progressValY, centerZ + progressValZ);
        GlStateManager.pushMatrix();
        if (mode.getValue() .equals("FULL")) {
            StayTessellator.drawBoxTests(axisAlignedBB1, (rainbow.getValue()) ? r : (red.getValue()), (rainbow.getValue()) ? g : (green.getValue()), (rainbow.getValue()) ? b : (blue.getValue()), alpha.getValue(), 63);
            StayTessellator.drawBoundingBox(axisAlignedBB1, 1.5f, (rainbow.getValue()) ? r : (red.getValue()), (rainbow.getValue()) ? g : (green.getValue()), (rainbow.getValue()) ? b : (blue.getValue()), 255);
        }
        if (mode.getValue().equals("SOLID")) {
            StayTessellator.drawBoxTests(axisAlignedBB1, (rainbow.getValue()) ? r : (red.getValue()), (rainbow.getValue()) ? g : (green.getValue()), (rainbow.getValue()) ? b : (blue.getValue()), alpha.getValue(), 63);
        }
        if (mode.getValue() .equals("OUTLINE")) {
            StayTessellator.drawBoundingBox(axisAlignedBB1, 1.5f, (rainbow.getValue()) ? r : (red.getValue()), (rainbow.getValue()) ? g : (green.getValue()), (rainbow.getValue()) ? b : blue.getValue(), alpha.getValue());
        }
        if (mode.getValue().equals("SOLIDFLAT")) {
            StayTessellator.drawBoxTests(axisAlignedBB1, (rainbow.getValue()) ? r : (red.getValue()), (rainbow.getValue()) ? g : (green.getValue()), (rainbow.getValue()) ? b : (blue.getValue()), alpha.getValue(), 63);
        }
        GlStateManager.popMatrix();
    }

    public float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        Vec3d offset = new Vec3d(entity.posX, entity.posY, entity.posZ);
        return calculateDamage(posX, posY, posZ, entity, offset);
    }

    public float calculateDamage(double posX, double posY, double posZ, Entity entity, Vec3d vec) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = getRange(vec, posX, posY, posZ) / doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (Exception ignore) {
        }
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (float) ((v * v + v) / 2.0 * 7.0 * doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            try {
                finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0f, false, true));
            } catch (Exception ignored) {
            }
        }
        return (float) finald;
    }

    @Override
    public void onEnable() {
        if(fullNullCheck()){
            return;
        }
        newSlot = mc.player.inventory.currentItem;
        oldSlot = mc.player.inventory.currentItem;
        fadeBlockSize.reset();
        lastEntityID = -1;
        CSlot = -1;
        ShouldStop = false;
        ShouldShadeRender = false;
        ShouldInfoLastBreak = false;
        ShouldDisableRender = true;
        switchCooldown = false;
        afterAttacking = false;
        canPredictHit = true;
        isActive = false;
        canMine = false;
        PlaceTimer.reset();
        ExplodeTimer.reset();
        PacketExplodeTimer.reset();
        SwitchTimer.reset();
        CalcTimer.reset();
        HoleMiningTimer.reset();
        packetList.clear();
    }

    @Override
    public void onDisable() {
        if(fullNullCheck()){
            return;
        }
        renderEnt = null;
        if (render != null) {
            ShouldDisableRender = true;
        }
        render = null;
        lastLookAt = Vec3d.ZERO;
        StuckTimes = 0;
        packetList.clear();
    }

    @Override
    public String getModuleInfo() {
        if (ShouldInfoLastBreak && lastBreakTime != 0L) {
            infoBreakTime = System.currentTimeMillis() - lastBreakTime;
            lastBreakTime = 0L;
            ShouldInfoLastBreak = false;
        }
        if (infoBreakTime != 0L) {
            double nmsl = 100.0;
            int cnm = 2;
            return TextFormatting.BOLD + String.format(String.valueOf((infoBreakTime / nmsl)), cnm) ;
        } else {
            return "Crystal";
        }
    }




    public static class CrystalTarget {
        public BlockPos blockPos;
        public Entity target;
        public double dmg;

        public CrystalTarget(BlockPos block, Entity target, double dmg) {
            this.blockPos = block;
            this.target = target;
            this.dmg = dmg;
        }
    }

}
