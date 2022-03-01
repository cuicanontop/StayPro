package dev.cuican.staypro.event.events.client;

import com.mojang.authlib.GameProfile;
import dev.cuican.staypro.event.StayEvent;

public class ConnectionEvent extends StayEvent
{
    public static int ADD_PLAYER    = 0;
    public static int REMOVE_PLAYER = 1;

    private final GameProfile Profile;

    public ConnectionEvent(final int stage, final GameProfile gameProfile) {
        super(stage);
        this.Profile = gameProfile;
    }

    public GameProfile getProfile() {
        return Profile;
    }
}