package dev.cuican.staypro.module.modules.misc;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.network.ClientChatReceivedEvent;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.ChatUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChat;

import java.util.Locale;

@ModuleInfo(name = "AntiRidicule", category = Category.MISC, description = "Prevent the opposite ez you")

public class AntiEz extends Module {




    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() != 0) {
            return;
        }
        if(event.getPacket() instanceof SPacketChat){
            SPacketChat packetChat = (SPacketChat)event.getPacket();
            String nAme = mc.player.getName();
            String msg =  packetChat.getChatComponent().getUnformattedText();
            if(msg.contains(nAme)&&(msg.toLowerCase().contains("ez")||msg.toLowerCase().contains("lll"))){
                event.setCancelled(true);
                ChatUtil.printChatMessage("Blocked mock messages for you - StayPro");
            }
        }
    }

}
