package dev.cuican.staypro.module.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.ChatEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;

import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
@ModuleInfo(name = "Timestamps", category = Category.MISC, description = "Prefixes chat messages with the time")
public class Timestamps extends Module {


    @Listener
    public void onClientChatReceived(ChatEvent event) {
        Date date = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
        String strDate = dateFormatter.format(date);
        TextComponentString time = new TextComponentString(ChatFormatting.RED + "<" + ChatFormatting.GRAY + strDate + ChatFormatting.RED + ">" + ChatFormatting.RESET + " ");
        System.out.println(time.getText()+event.getMessage());
        event.setMessage(time.getText()+event.getMessage());
    }
}