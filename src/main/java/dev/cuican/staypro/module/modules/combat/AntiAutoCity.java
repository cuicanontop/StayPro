package dev.cuican.staypro.module.modules.combat;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.utils.BurrowUtil;
import dev.cuican.staypro.utils.HoleUtil;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.List;

@ModuleInfo(name = "AntiAutoCity", category = Category.COMBAT, description = "AntiAutoCity")
public class AntiAutoCity extends Module {

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if(event.getPacket() instanceof SPacketBlockChange&& HoleUtil.isInHole()){
            SPacketBlockChange packet =(SPacketBlockChange) event.getPacket();
            BlockPos  blockpos = packet.getBlockPosition();

            if(blockpos.equals(new BlockPos(mc.player.posX+1,mc.player.posY,mc.player.posZ)))placeBlock(new BlockPos(mc.player.posX+2,mc.player.posY,mc.player.posZ));
            if(blockpos.equals(new BlockPos(mc.player.posX,mc.player.posY,mc.player.posZ+1)))placeBlock(new BlockPos(mc.player.posX,mc.player.posY,mc.player.posZ+2));
            if(blockpos.equals(new BlockPos(mc.player.posX-1,mc.player.posY,mc.player.posZ)))placeBlock(new BlockPos(mc.player.posX+-2,mc.player.posY,mc.player.posZ));
            if(blockpos.equals(new BlockPos(mc.player.posX,mc.player.posY,mc.player.posZ-1)))placeBlock(new BlockPos(mc.player.posX,mc.player.posY,mc.player.posZ-2));
        }
    }


    private void placeBlock(BlockPos pos){
        if(!mc.world.isAirBlock(pos))return;
        int slot = mc.player.inventory.currentItem;
        int blockslot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        if(blockslot==-1)return;
        mc.player.connection.sendPacket(new CPacketHeldItemChange(blockslot));
        BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND,false,false);
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
    }




}
