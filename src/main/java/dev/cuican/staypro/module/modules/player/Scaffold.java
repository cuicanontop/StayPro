package dev.cuican.staypro.module.modules.player;


import com.mojang.realmsclient.gui.ChatFormatting;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.UpdateWalkingPlayerEvent;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.mixin.accessor.AccessorMinecraft;
import dev.cuican.staypro.mixin.accessor.AccessorTimer;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.GeometryMasks;
import dev.cuican.staypro.utils.Wrapper;
import dev.cuican.staypro.utils.block.BlockInteractionHelper;
import dev.cuican.staypro.utils.graphics.StayTessellator;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import dev.cuican.staypro.utils.look;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "Scaffold", category = Category.PLAYER)
public class Scaffold extends Module {
    public Setting<String> mode = setting("TowerMode", "NORMAL",listOf(
            "AAC",
            "NCP",
            "TP",
            "SPARTAN",
            "LONG",
            "TIMER",
            "NORMAL"));
    public Setting<Integer> timer = setting("Timer",3,1,10).whenAtMode(mode, "TIMER");
    public Setting<Boolean> Obsidian = setting("Obsidian",true);
    public boolean rotated;
    BlockPos lastPos;
    int newSlot;
    int resetTimer;
    public long time;
    public final List<Block> blackList;


    public Scaffold() {
        blackList = Arrays.asList(Blocks.CHEST, Blocks.TRAPPED_CHEST);
        rotated = false;
        newSlot = -1;
        lastPos = null;
    }



    public static IBlockState getState(final BlockPos blockPos) {
        return Wrapper.getWorld().getBlockState(blockPos);
    }

    public static EnumActionResult processRightClickBlock(final BlockPos blockPos, final EnumFacing enumFacing, final Vec3d vec3d) {
        return mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing, vec3d, EnumHand.MAIN_HAND);
    }

    public static Block getBlock(final BlockPos blockPos) {
        return getState(blockPos).getBlock();
    }



    public static boolean canBeClicked(final BlockPos blockPos) {
        return getBlock(blockPos).canCollideCheck(getState(blockPos), false);
    }

    public static PlayerControllerMP getPlayerController() {
        return Minecraft.getMinecraft().playerController;
    }

    @Listener
    public void lambda$new$1(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0) {
            if (mc.player == null || ModuleManager.getModuleByName("Freecam").isEnabled()) {
                return;
            }
            lastPos = null;
            final Vec3d interpolatedPos = EntityUtil.getInterpolatedPos(mc.player, mc.getRenderPartialTicks());
            BlockPos down = new BlockPos(new Vec3d(interpolatedPos.x, Math.floor(mc.player.posY), interpolatedPos.z)).down();
            final BlockPos down2 = down.down();
            if (!Wrapper.getWorld().getBlockState(down).getMaterial().isReplaceable()) {
                ++resetTimer;
                return;
            }
            newSlot = -1;
            for (int i = 0; i < 9; ++i) {
                final ItemStack getStackInSlot = mc.player.inventory.getStackInSlot(i);
                if (getStackInSlot != ItemStack.EMPTY && getStackInSlot.getItem() instanceof ItemBlock) {
                    final Block getBlock = ((ItemBlock) getStackInSlot.getItem()).getBlock();
                    if (!blackList.contains(getBlock) && (!(((ItemBlock) getStackInSlot.getItem()).getBlock() instanceof BlockFalling) || !Wrapper.getWorld().getBlockState(down2).getMaterial().isReplaceable())) {
                        newSlot = i;
                        break;
                    }
                }
            }
            if (newSlot == -1) {
                return;
            }
            lastPos = down;
            Label_0412:
            {
                if (!hasNeighbour(down)) {
                    final EnumFacing[] values = EnumFacing.values();
                    for (int length = values.length, j = 0; j < length; ++j) {
                        final BlockPos offset = down.offset(values[j]);
                        if (hasNeighbour(offset)) {
                            down = offset;
                            lastPos = down;
                            break Label_0412;
                        }
                    }
                    return;
                }
            }
            if (mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(down)).isEmpty() && mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(down)).isEmpty()) {
                final Vec3d vec3d = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
                for (final EnumFacing enumFacing : EnumFacing.values()) {
                    final BlockPos offset2 = down.offset(enumFacing);
                    final EnumFacing getOpposite = enumFacing.getOpposite();
                    if (canBeClicked(offset2)) {
                        final Vec3d add = new Vec3d(offset2).add(0.5, 0.5, 0.5).add(new Vec3d(getOpposite.getDirectionVec()).scale(0.5));
                        if (vec3d.squareDistanceTo(add) <= 18.0625) {
                            double n2 = enumFacing.equals(EnumFacing.UP) ? (add.y - 0.5) : (add.y + 0.5);
                            final float[] legitRotations = BlockInteractionHelper.getLegitRotations(new Vec3d(add.x, n2, add.z));
                            look.lookAt(new Vec3d(offset2.getX() + 0.5 , offset2.getY() - 1 , offset2.getZ() + 0.5));
                            lastPos = down;
                            return;
                        }
                    }
                }
            }
            lastPos = null;
        }
    }

    @Override
    public void onRenderWorld(RenderEvent event) {
        if (lastPos != null) {
            StayTessellator.prepare(GL11.GL_QUADS);
            StayTessellator.drawBox(lastPos, 0x30F56674, GeometryMasks.Quad.ALL);
            StayTessellator.release();
            StayTessellator.prepare(GL11.GL_QUADS);
            StayTessellator.drawBoundingBoxBlockPos(lastPos, 1f, 255, 192, 203, 200);
            StayTessellator.release();
        }
    }

    @Override
    public String getModuleInfo() {
        return ChatFormatting.RED + mode.getValue();
    }

    @Listener
    public void update(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1) {
            if (lastPos == null) {
                ++resetTimer;
                return;
            }
            newSlot = -1;
            if(Obsidian.getValue()){
                newSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            }else {
                for (int i = 0; i < 9; ++i) {
                    final ItemStack getStackInSlot = mc.player.inventory.getStackInSlot(i);
                    if (getStackInSlot != ItemStack.EMPTY && getStackInSlot.getItem() instanceof ItemBlock) {
                        final Block getBlock = ((ItemBlock) getStackInSlot.getItem()).getBlock();
                        if (!blackList.contains(getBlock) && !(((ItemBlock) getStackInSlot.getItem()).getBlock() instanceof BlockFalling)) {
                            newSlot = i;
                            break;
                        }
                    }
                    InventoryUtil.switchToHotbarSlot(newSlot, false);
                }
            }

            if (newSlot == -1) {
                return;
            }
            final int currentItem = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = newSlot;
            if (!placeBlockScaffold(lastPos)) {
                ++resetTimer;
            } else {
                rotated = true;
                resetTimer = 0;
            }
            InventoryUtil.switchToHotbarSlot(currentItem, false);
        }
    }

    public boolean hasNeighbour(final BlockPos blockPos) {
        final EnumFacing[] values = EnumFacing.values();
        for (int length = values.length, i = 0; i < length; ++i) {
            if (!Wrapper.getWorld().getBlockState(blockPos.offset(values[i])).getMaterial().isReplaceable()) {
                return true;
            }
        }
        return false;
    }

    public void reset() {
        time = System.nanoTime();
    }

    @Override
    public void onDisable() {
        if (fullNullCheck()) {
            return;
        }

        resetTimer = 0;
        ((AccessorTimer)((AccessorMinecraft)Minecraft.getMinecraft()).aqGetTimer()).aqSetTickLength(50.0f);
        rotated = false;
    }

    public boolean placeBlockScaffold(final BlockPos blockPos) {
        if (mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(blockPos)).isEmpty() && mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(blockPos)).isEmpty()) {
            final Vec3d vec3d = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
            for (final EnumFacing enumFacing : EnumFacing.values()) {
                final BlockPos offset = blockPos.offset(enumFacing);
                final EnumFacing getOpposite = enumFacing.getOpposite();
                if (canBeClicked(offset)) {
                    final Vec3d add = new Vec3d(offset).add(0.5, 0.5, 0.5).add(new Vec3d(getOpposite.getDirectionVec()).scale(0.5));
                    if (vec3d.squareDistanceTo(add) <= 18.0625) {
                        mc.playerController.processRightClickBlock(mc.player, mc.world, offset, getOpposite, vec3d, EnumHand.MAIN_HAND);
                        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                        ((AccessorMinecraft) mc).setRightClickDelayTimer(4);
                        if (mode.getValue().equals("NORMAL") && mc.player.moveStrafing == 0.0f && mc.player.moveForward == 0.0f && mc.gameSettings.keyBindJump.isKeyDown()) {
                            mc.player.jump();
                            mc.player.motionX *= 0.3;
                            mc.player.motionZ *= 0.3;
                        }
                        if (mode.getValue().equals("NCP")) {
                            double blockBelow = -2.0D;
                            if (mc.gameSettings.keyBindJump.isPressed()) {
                                mc.player.jump();
                                mc.player.motionY = 0.41999998688697815D;
                            }
                            if (mc.player.motionY < 0.1D && !(mc.world.getBlockState((new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).add(0.0D, blockBelow, 0.0D)).getBlock() instanceof net.minecraft.block.BlockAir)) {
                                mc.player.motionY = -10.0D;
                            }
                        }
                        if (mode.getValue().equals("TP") && mc.gameSettings.keyBindJump.isKeyDown()) {
                            mc.player.jump();
                            mc.player.motionY -= 0.2300000051036477D;
                            mc.player.setPosition(mc.player.posX, mc.player.posY + 1.1D, mc.player.posZ);
                        }
                        if(mode.getValue().equals("SPARTAN")){
                            double blockBelow = -2.0D;
                            if (mc.gameSettings.keyBindJump.isPressed()) {
                                mc.player.jump();
                                mc.player.motionY = 0.41999998688697815D;
                            }
                            if (mc.player.motionY < 0.0D && !(mc.world.getBlockState((new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).add(0.0D, blockBelow, 0.0D)).getBlock() instanceof net.minecraft.block.BlockAir)) {
                                mc.player.motionY = -10.0D;
                            }
                        }
                        if(mode.getValue().equals("AAC")){
                            if (mc.gameSettings.keyBindJump.isPressed()) {
                                mc.player.jump();
                                mc.player.motionY = 0.395D;
                                mc.player.motionY -= 0.002300000051036477D;
                            }
                        }
                        if(mode.getValue().equals("LONG") && mc.gameSettings.keyBindJump.isKeyDown()) {
                            mc.player.jump();
                            if (EntityUtil.isMoving()) {
                                if (isOnGround(0.76D) && !isOnGround(0.75D) && mc.player.motionY > 0.23D && mc.player.motionY < 0.25D) {
                                    double round = Math.round(mc.player.posY);
                                    mc.player.motionY = round - mc.player.posY;
                                }
                                if (isOnGround(1.0E-4D)) {
                                    mc.player.motionY = 0.42D;
                                    mc.player.motionX *= 0.9D;
                                    mc.player.motionZ *= 0.9D;
                                } else if (mc.player.posY >= Math.round(mc.player.posY) - 1.0E-4D &&
                                        mc.player.posY <= Math.round(mc.player.posY) + 1.0E-4D) {
                                    mc.player.motionY = 0.0D;
                                }
                            } else {
                                mc.player.motionX = 0.0D;
                                mc.player.motionZ = 0.0D;
                                mc.player.jumpMovementFactor = 0.0F;
                                double x = mc.player.posX;
                                double y = mc.player.posY - 1.0D;
                                double z = mc.player.posZ;
                                BlockPos blockBelow = new BlockPos(x, y, z);
                                if (mc.world.getBlockState(blockBelow).getBlock() == Blocks.AIR) {
                                    mc.player.motionY = 0.4196D;
                                    mc.player.motionX *= 0.75D;
                                    mc.player.motionZ *= 0.75D;
                                }
                            }
                        }
                        if(mode.getValue().equals("TIMER")){
                            if (!mc.player.onGround) {

                                ((AccessorTimer)((AccessorMinecraft)Minecraft.getMinecraft()).aqGetTimer()).aqSetTickLength((float) (50.0D / this.timer.getValue()));
                            }
                            ((AccessorMinecraft) mc).setRightClickDelayTimer(0);
                            if (mc.player.onGround) {
                                mc.player.motionY = 0.3932D;
                                ((AccessorTimer)((AccessorMinecraft)Minecraft.getMinecraft()).aqGetTimer()).aqSetTickLength(50.0F);
                            }
                        }
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isOnGround(double height) {
        return !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }
}
