package dev.cuican.staypro.event.decentraliized;

import dev.cuican.staypro.concurrent.decentralization.DecentralizedEvent;
import dev.cuican.staypro.event.events.network.PacketEvent;

public class DecentralizedPacketEvent {
    public static class Send extends DecentralizedEvent<PacketEvent.Send> {
        public static Send instance = new Send();
    }

    public static class Receive extends DecentralizedEvent<PacketEvent.Receive> {
        public static Receive instance = new Receive();
    }
}
