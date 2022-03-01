package dev.cuican.staypro.event.events.client;

import dev.cuican.staypro.event.StayEvent;

public class ChatEvent extends StayEvent {

    protected String message;

    public ChatEvent(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public final String getMessage() {
        return this.message;
    }

}