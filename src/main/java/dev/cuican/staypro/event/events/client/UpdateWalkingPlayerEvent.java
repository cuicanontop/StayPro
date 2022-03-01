package dev.cuican.staypro.event.events.client;


import dev.cuican.staypro.event.StayEvent;

public class UpdateWalkingPlayerEvent
        extends StayEvent {
    public UpdateWalkingPlayerEvent(int stage) {
        super(stage);
    }
}

