
package dev.cuican.staypro.module.modules.combat;


import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.render.GuiNewChatEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.block.BlockInteractionHelper;
import dev.cuican.staypro.utils.graphics.AnimationUtil;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Comparator;
import java.util.HashMap;
@ModuleInfo(name = "Anti32k", description = "Anti32k",category = Category.COMBAT)
public class Anti32k
        extends Module {
    private static Anti32k INSTANCE = new Anti32k();
    private Setting<Integer> range = setting("Range", 5, 3, 8);
    private Setting<Boolean> packetMine = setting("PacketMine", false);
    public static BlockPos min = null;

    int oldslot = -1;
    int shulkInt;
    HashMap<BlockPos, Integer> opendShulk = new HashMap();

    public AnimationUtil animationUtils = new AnimationUtil();

    @Override
    public void onDisable() {
        this.oldslot = -1;
        this.shulkInt = 0;
        this.opendShulk.clear();
    }


    @Override
    public void onTick() {
        if (Anti32k.fullNullCheck()) {
            return;
        }
        Auto32k Auto32k = (Auto32k) ModuleManager.getModuleByName("AutoXin32k");

        BlockPos hopperPos = (BlockPos) BlockInteractionHelper.getSphere(EntityUtil.getLocalPlayerPosFloored(), (float)(Integer)this.range.getValue(), (Integer)this.range.getValue(), false, true, 0).stream().filter((e) -> {
            return mc.world.getBlockState(e).getBlock() instanceof BlockHopper && mc.world.getBlockState(new BlockPos(e.getX(),e.getY()+1,e.getZ())).getBlock() instanceof BlockShulkerBox&&!e.equals(Auto32k.placeTarget);
        }).min(Comparator.comparing((e) -> {
            return mc.player.getDistanceSq(e);
        })).orElse(null);
        int slot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE) ;
        if (slot != -1&&hopperPos!=null) {

                if (mc.player.getDistance((double) hopperPos.getX(), (double) hopperPos.getY(), (double) hopperPos.getZ()) > (double)(Integer)this.range.getValue()) {
                    return;
                }

                if (mc.player.inventory.currentItem != slot) {
                    this.oldslot = mc.player.inventory.currentItem;
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
                }

                if ((Boolean)this.packetMine.getValue()) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, hopperPos, EnumFacing.UP));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, hopperPos, EnumFacing.UP));
                } else {
                    mc.playerController.onPlayerDamageBlock(hopperPos, EnumFacing.UP);
                    mc.playerController.onPlayerDestroyBlock(hopperPos);
                }

                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                if (this.oldslot != -1) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(this.oldslot));
                }


        }
    }




}

