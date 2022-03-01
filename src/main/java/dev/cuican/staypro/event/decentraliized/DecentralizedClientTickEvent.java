package dev.cuican.staypro.event.decentraliized;

import dev.cuican.staypro.concurrent.decentralization.DecentralizedEvent;
import dev.cuican.staypro.concurrent.decentralization.EventData;

public class DecentralizedClientTickEvent extends DecentralizedEvent<EventData> {
    public static DecentralizedClientTickEvent instance = new DecentralizedClientTickEvent();
}
