package dev.cuican.staypro.module.modules.misc;

import dev.cuican.staypro.Stay;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.mixin.accessor.AccessorCPacketChatMessage;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.Objects;

@Parallel
@ModuleInfo(name = "CustomChat", category = Category.MISC, description = "Append a suffix on chat message")
public class CustomChat extends Module {

    Setting<Boolean> commands = setting("Commands", false);

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof CPacketChatMessage) {
            if( Objects.requireNonNull(ModuleManager.getModuleByName("AutoQueue")).isEnabled())return;

            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (s.startsWith("/") && !commands.getValue()) return;
            s =s+" ♛STAY✘PRO"+Stay.MOD_VERSION+"℡";
            if (s.length() >= 256) s = s.substring(0, 256);
            ((AccessorCPacketChatMessage) event.getPacket()).setMessage(s);
        }
    }

}
