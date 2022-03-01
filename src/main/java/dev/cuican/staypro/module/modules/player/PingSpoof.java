package dev.cuican.staypro.module.modules.player;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.PacketEvents;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

@ModuleInfo(name = "PingSpoof", category = Category.PLAYER, description = "Cancels server side packets")
public class PingSpoof extends Module {
    private final Setting<Integer> pingSpoof =setting ("Ping", 100, 1, 10000);

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketKeepAlive) {
            event.isCancelled();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketKeepAlive(((SPacketKeepAlive) event.getPacket()).getId()));
                }
            }, this.pingSpoof.getValue());
        }
    }

    @Override
    public String getModuleInfo() {
        return String.valueOf(this.pingSpoof.getValue());
    }
}
