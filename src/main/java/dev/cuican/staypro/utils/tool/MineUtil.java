package dev.cuican.staypro.utils.tool;


import dev.cuican.staypro.module.pingbypass.util.Globals;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.init.Enchantments.EFFICIENCY;

public class MineUtil implements Globals
{



    public static int findBestTool(BlockPos pos)
    {
        return findBestTool(pos, mc.world.getBlockState(pos));
    }

    public static int findBestTool(BlockPos pos, IBlockState state)
    {
        int result = mc.player.inventory.currentItem;
        if (state.getBlockHardness(mc.world, pos) > 0)
        {
            double speed = getSpeed(state, mc.player.getHeldItemMainhand());
            for (int i = 0; i < 9; i++)
            {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                double stackSpeed = getSpeed(state, stack);
                if (stackSpeed > speed)
                {
                    speed  = stackSpeed;
                    result = i;
                }
            }
        }

        return result;
    }

    public static double getSpeed(IBlockState state, ItemStack stack)
    {
        double str = stack.getDestroySpeed(state);
        int effect = EnchantmentHelper.getEnchantmentLevel(EFFICIENCY, stack);
        return Math.max(str + (str > 1.0 ? (effect * effect + 1.0) : 0.0), 0.0);
    }





    public static float getDigSpeed(ItemStack stack, IBlockState state, boolean onGround)
    {
        float digSpeed = 1.0F;

        if (!stack.isEmpty())
        {
            digSpeed *= stack.getDestroySpeed(state);
        }

        if (digSpeed > 1.0F)
        {
            int i = EnchantmentHelper.getEnchantmentLevel(EFFICIENCY, stack);

            if (i > 0 && !stack.isEmpty())
            {
                digSpeed += (float)(i * i + 1);
            }
        }

        if (mc.player.isPotionActive(MobEffects.HASTE))
        {
            //noinspection ConstantConditions
            digSpeed *= 1.0F 
                + (mc.player.getActivePotionEffect(MobEffects.HASTE)
                            .getAmplifier() + 1) * 0.2F;
        }

        if (mc.player.isPotionActive(MobEffects.MINING_FATIGUE))
        {
            float miningFatigue;
            //noinspection ConstantConditions
            switch (mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE)
                             .getAmplifier())
            {
                case 0:
                    miningFatigue = 0.3F;
                    break;
                case 1:
                    miningFatigue = 0.09F;
                    break;
                case 2:
                    miningFatigue = 0.0027F;
                    break;
                case 3:
                default:
                    miningFatigue = 8.1E-4F;
            }

            digSpeed *= miningFatigue;
        }

        if (mc.player.isInsideOfMaterial(Material.WATER) 
                && !EnchantmentHelper.getAquaAffinityModifier(mc.player))
        {
            digSpeed /= 5.0F;
        }

        if (onGround && !mc.player.onGround)
        {
            digSpeed /= 5.0F;
        }

        return (digSpeed < 0 ? 0 : digSpeed);
    }

    public static boolean canBreak(BlockPos pos)
    {
        return canBreak(mc.world.getBlockState(pos), pos);
    }

    public static boolean canBreak(IBlockState state, BlockPos pos)
    {
        return state.getBlockHardness(mc.world, pos) != -1
                || state.getMaterial().isLiquid();
    }
    


}
