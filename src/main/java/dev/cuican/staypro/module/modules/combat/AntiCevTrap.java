//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.cuican.staypro.module.modules.combat;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.MathUtil;
import dev.cuican.staypro.utils.block.BlockUtil;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@ModuleInfo(name = "AntiCev", category = Category.COMBAT, description = "AntiCev")
public class AntiCevTrap extends Module {
    public Setting<Boolean> rotate = setting("Rotate", true);
    public Setting<Boolean> packet =setting("Packet", true);
    private float yaw = 0.0F;
    private float pitch = 0.0F;
    private boolean rotating = false;
    private boolean isSneaking;



    @Override
    public void onTick() {
        if (!fullNullCheck()) {
            if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1) {
                this.main();
            }
        }
    }

    @Override
    public void onDisable() {
        this.rotating = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if ( this.rotate.getValue() && this.rotating && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            packet.yaw = this.yaw;
            packet.pitch = this.pitch;
            this.rotating = false;
        }

    }
    public static final List<BlockPos> cevBlocks = Arrays.asList(
            new BlockPos(0, 1, -1),
            new BlockPos(-1, 1, 0),
            new BlockPos(1, 1, 0),
            new BlockPos(0, 1, 1),
            new BlockPos(1, 1, 1),
            new BlockPos(1, 1, -1),
            new BlockPos(-1, 1, 1),
            new BlockPos(-1, 1, -1)
    );

    private void main() {
        for (BlockPos pos :cevBlocks){
            Vec3d a = mc.player.getPositionVector();
            if (this.checkTrap(a, EntityUtil.getVarOffsets(pos.x, pos.y, pos.z)) && this.checkCrystal(a, EntityUtil.getVarOffsets(pos.x, pos.y+1, pos.z)) != null) {

                if (this.packet.getValue()) {
                    this.rotateTo(this.checkCrystal(a, EntityUtil.getVarOffsets(pos.x, pos.y+1, pos.z)));
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(pos.x, pos.y+1, pos.z)), true);
                } else {
                    this.rotateTo(this.checkCrystal(a, EntityUtil.getVarOffsets(0, pos.y+1, 0)));
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(pos.x, pos.y+1, pos.z)), false);
                }

                this.rotateToPos(a, EntityUtil.getVarOffsets(pos.x, pos.y+1, pos.z));
                this.place(a, EntityUtil.getVarOffsets(pos.x, pos.y+1, pos.z));
            }

            if (this.checkTrap(a, EntityUtil.getOffsets(2, false)) && this.checkTrap(a, EntityUtil.getVarOffsets(0, 2, 0)) && this.checkCrystal(a, EntityUtil.getVarOffsets(0, 3, 0)) != null) {
                ++mc.player.motionY;
                if ((Boolean)this.packet.getValue()) {
                    this.rotateTo(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 3, 0)));
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 3, 0)), true);
                } else {
                    this.rotateTo(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 3, 0)));
                    EntityUtil.attackEntity(this.checkCrystal(a, EntityUtil.getVarOffsets(0, 3, 0)), false);
                }

                this.rotateToPos(a, EntityUtil.getVarOffsets(0, 3, 0));
                this.place(a, EntityUtil.getVarOffsets(0, 3, 0));
            }
        }



    }

    private void rotateTo(Entity entity) {
        if (this.rotate.getValue()) {
            float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
            this.yaw = angle[0];
            this.pitch = angle[1];
            this.rotating = true;
        }

    }

    private void rotateToPos(Vec3d pos, Vec3d[] list) {
        if (this.rotate.getValue()) {
            Vec3d[] var3 = list;
            int var4 = list.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Vec3d vec3d = var3[var5];
                BlockPos position = (new BlockPos(pos)).add(vec3d.x, vec3d.y, vec3d.z);
                float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((double)((float)position.getX() + 0.5F), (double)((float)position.getY() - 0.5F), (double)((float)position.getZ() + 0.5F)));
                this.yaw = angle[0];
                this.pitch = angle[1];
                this.rotating = true;
            }
        }

    }

    private void place(Vec3d pos, Vec3d[] list) {
        Vec3d[] var3 = list;
        int var4 = list.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Vec3d vec3d = var3[var5];
            BlockPos position = (new BlockPos(pos)).add(vec3d.x, vec3d.y, vec3d.z);
            int a = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            mc.playerController.updateController();
            this.isSneaking = BlockUtil.placeBlock(position, EnumHand.MAIN_HAND, false, (Boolean)this.packet.getValue(), true);
            mc.player.inventory.currentItem = a;
            mc.playerController.updateController();
        }

    }

    Entity checkCrystal(Vec3d pos, Vec3d[] list) {
        Entity crystal = null;
        Vec3d[] var4 = list;
        int var5 = list.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Vec3d vec3d = var4[var6];
            BlockPos position = (new BlockPos(pos)).add(vec3d.x, vec3d.y, vec3d.z);
            Iterator var9 = mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(position)).iterator();

            while(var9.hasNext()) {
                Entity entity = (Entity)var9.next();
                if (entity instanceof EntityEnderCrystal && crystal == null) {
                    crystal = entity;
                }
            }
        }

        return crystal;
    }

    private boolean checkTrap(Vec3d pos, Vec3d[] list) {
        Vec3d[] var3 = list;
        int var4 = list.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Vec3d vec3d = var3[var5];
            BlockPos position = (new BlockPos(pos)).add(vec3d.x, vec3d.y, vec3d.z);
            Block block = EntityUtil.mc.world.getBlockState(position).getBlock();
            if (block == Blocks.OBSIDIAN) {
                return true;
            }
        }

        return false;
    }
}
