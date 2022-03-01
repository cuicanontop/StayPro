package dev.cuican.staypro.utils;

import dev.cuican.staypro.mixin.accessor.AccessorRenderManager;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CrystalUtil {
    protected static final double getDirection2D(double dx, double dy)
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

    public static double getDamage(Vec3d pos, @Nullable Entity target) {
        Entity entity = target == null ? mc.player : target;
        float damage = 6.0F;
        float f3 = damage * 2.0F;
        Vec3d vec3d = pos;

        if (!entity.isImmuneToExplosions())
        {
            double d12 = entity.getDistance(pos.x, pos.y, pos.z) / (double)f3;

            if (d12 <= 1.0D)
            {
                double d5 = entity.posX - pos.x;
                double d7 = entity.posY + (double)entity.getEyeHeight() - pos.y;
                double d9 = entity.posZ - pos.z;
                double d13 = (double)MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);

                if (d13 != 0.0D)
                {
                    d5 = d5 / d13;
                    d7 = d7 / d13;
                    d9 = d9 / d13;
                    double d14 = (double)mc.world.getBlockDensity(pos, entity.getEntityBoundingBox());
                    double d10 = (1.0D - d12) * d14;
                    return (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)f3 + 1.0D));
                }
            }
        }
        return 0;
    }
    protected static final Vec3d getVectorForRotation(double pitch, double yaw)
    {
        float f = MathHelper.cos((float) (-yaw * 0.017453292F - (float)Math.PI));
        float f1 = MathHelper.sin((float) (-yaw * 0.017453292F - (float)Math.PI));
        float f2 = -MathHelper.cos((float) (-pitch * 0.017453292F));
        float f3 = MathHelper.sin((float) (-pitch * 0.017453292F));
        return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
    }

    public static EnumActionResult placeCrystal(BlockPos pos) {
        pos.offset(EnumFacing.DOWN);
        double dx=(pos.getX()+0.5-mc.player.posX);
        double dy=(pos.getY()+0.5-mc.player.posY) - .5 -mc.player.getEyeHeight();
        double dz=(pos.getZ()+0.5-mc.player.posZ);

        double x=getDirection2D(dz, dx);
        double y=getDirection2D(dy, Math.sqrt(dx*dx+dz*dz));

        Vec3d vec = getVectorForRotation(-y, x-90);
        if (mc.player.inventory.offHandInventory.get(0).getItem().getClass().equals(Item.getItemById(426).getClass())) {
            return mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(EnumFacing.DOWN), EnumFacing.UP, vec, EnumHand.OFF_HAND);
        }else if (InventoryUtil.pickItem(426, false) != -1) {
            InventoryUtil.setSlot(InventoryUtil.pickItem(426, false));
            return mc.playerController.processRightClickBlock(mc.player, mc.world, pos.offset(EnumFacing.DOWN), EnumFacing.UP, vec, EnumHand.MAIN_HAND);
        }
        return EnumActionResult.FAIL;
    }
    public static Minecraft mc = Minecraft.getMinecraft();
    public static List<BlockPos> getSphere(Vec3d loc, double r, double h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = (int) loc.x;
        int cy = (int) loc.y;
        int cz = (int) loc.z;
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static double getRange(Vec3d a, double x, double y, double z) {
        double xl = a.x - x;
        double yl = a.y - y;
        double zl = a.z - z;
        return Math.sqrt(xl * xl + yl * yl + zl * zl);
    }

    public static boolean isReplaceable(Block block) {
        return block == Blocks.FIRE
                || block == Blocks.DOUBLE_PLANT
                || block == Blocks.VINE;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity, Vec3d vec) {
        float doubleExplosionSize = 12.0F;
        double distanceSize = getRange(vec, posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);

        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());

        double v = (1.0D - distanceSize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finalValue = 1.0;

        if (entity instanceof EntityLivingBase) {
            // we pass null as the exploder here
            //noinspection ConstantConditions
            finalValue = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
        }
        return (float) finalValue;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        Vec3d offset = new Vec3d(entity.posX, entity.posY, entity.posZ);
        return calculateDamage(posX, posY, posZ, entity, offset);
    }

    private static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        try {
            if (entity instanceof EntityPlayer) {
                EntityPlayer ep = (EntityPlayer) entity;
                DamageSource ds = DamageSource.causeExplosionDamage(explosion);
                damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

                int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
                float f = MathHelper.clamp(k, 0.0F, 20.0F);
                damage = damage * (1.0F - f / 25.0F);

                if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                    damage = damage - (damage / 5);
                }

                damage = Math.max(damage, 0.0f);
                return damage;
            }
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            return damage;
        } catch (Exception ignored) {
            return getBlastReduction(entity, damage, explosion);
        }
    }

    private static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static EnumFacing enumFacing(final BlockPos blockPos) {
        final EnumFacing[] values;
        final int length = (values = EnumFacing.values()).length;
        int i = 0;
        while (i < length) {
            final EnumFacing enumFacing = values[i];
            final Vec3d vec3d = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
            final Vec3d vec3d2 = new Vec3d(blockPos.getX() + enumFacing.getDirectionVec().getX(), blockPos.getY() + enumFacing.getDirectionVec().getY(), blockPos.getZ() + enumFacing.getDirectionVec().getZ());
            final RayTraceResult rayTraceBlocks;
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

    public static boolean isEating() {
        return mc.player != null && (mc.player.getHeldItemMainhand().getItem() instanceof ItemFood || mc.player.getHeldItemOffhand().getItem() instanceof ItemFood) && mc.player.isHandActive();
    }

    public static boolean canSeeBlock(BlockPos p_Pos) {
        if (mc.player == null)
            return true;

        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(p_Pos.getX(), p_Pos.getY(), p_Pos.getZ()), false, true, false) != null;
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static double getVecDistance(BlockPos a, double posX, double posY, double posZ) {
        double x1 = a.getX() - posX;
        double y1 = a.getY() - posY;
        double z1 = a.getZ() - posZ;
        return Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
    }

    public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player) {
        glBillboard(x, y, z);
        int distance = (int) player.getDistance(x, y, z);
        float scaleDistance = (distance / 2.0f) / (2.0f + (2.0f - (float) 1));
        if (scaleDistance < 1f)
            scaleDistance = 1;
        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }

    public static void glBillboard(float x, float y, float z) {
        float scale = 0.016666668f * 1.6f;
        GlStateManager.translate(x - ((AccessorRenderManager) Minecraft.getMinecraft().getRenderManager()).getRenderPosX(), y - ((AccessorRenderManager) Minecraft.getMinecraft().getRenderManager()).getRenderPosY(),
                z - ((AccessorRenderManager) Minecraft.getMinecraft().getRenderManager()).getRenderPosZ());
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-Minecraft.getMinecraft().player.rotationYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(Minecraft.getMinecraft().player.rotationPitch, Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
    }

}
