package dev.cuican.staypro.module.modules.misc;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.event.events.network.PacketEvent;

import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


/**
 * Created by GlowskiBroski on 10/14/2018.
 */
@ModuleInfo(name = "PortalGodMode", category = Category.MISC, description = "PortalGodMode")

public class PortalGodMode extends Module {

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (isEnabled() && event.getPacket() instanceof CPacketConfirmTeleport) {
            event.setCancelled(true);
        }
    }
}
