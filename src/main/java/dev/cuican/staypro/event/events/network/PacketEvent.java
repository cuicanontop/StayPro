package dev.cuican.staypro.event.events.network;

import dev.cuican.staypro.concurrent.decentralization.EventData;
import dev.cuican.staypro.event.StayEvent;
import net.minecraft.network.Packet;

public class PacketEvent extends StayEvent implements EventData {

    public final Packet<?> packet;

    public PacketEvent(final Packet<?> packet) {
        this.packet = packet;
    }

    public static class Receive extends PacketEvent {
        public Receive(final Packet<?> packet) {
            super(packet);
        }
    }

    public static class Send extends PacketEvent {
        public Send(final Packet<?> packet) {
            super(packet);
        }
    }

    public Packet<?> getPacket() {
        return packet;
    }

}
