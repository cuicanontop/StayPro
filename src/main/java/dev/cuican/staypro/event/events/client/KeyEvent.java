package dev.cuican.staypro.event.events.client;

import dev.cuican.staypro.event.StayEvent;

public final class KeyEvent extends StayEvent {

    private final int key;
    private final char character;

    public boolean pressed;




    public KeyEvent(int key, char character) {

        this.key = key;
        this.character = character;
    }

    public final int getKey() {
        return this.key;
    }

    public final char getCharacter() {
        return this.character;
    }

}