/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.MovementInput
 */
package dev.cuican.staypro.event.events.network;


import net.minecraft.util.MovementInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlayerUpdateMoveStateEvent {
    @NotNull
    private final MovementInput movementInput;

    public PlayerUpdateMoveStateEvent(@NotNull MovementInput movementInput) {
        this.movementInput = movementInput;
    }

    @NotNull
    public final MovementInput getMovementInput() {
        return this.movementInput;
    }

    @NotNull
    public final MovementInput component1() {
        return this.movementInput;
    }

    @NotNull
    public final PlayerUpdateMoveStateEvent copy(@NotNull MovementInput movementInput) {
        return new PlayerUpdateMoveStateEvent(movementInput);
    }

    public static /* synthetic */ PlayerUpdateMoveStateEvent copy$default(PlayerUpdateMoveStateEvent playerUpdateMoveStateEvent, MovementInput movementInput, int n, Object object) {
        if ((n & 1) == 0) return playerUpdateMoveStateEvent.copy(movementInput);
        movementInput = playerUpdateMoveStateEvent.movementInput;
        return playerUpdateMoveStateEvent.copy(movementInput);
    }

    @NotNull
    public String toString() {
        return "PlayerUpdateMoveStateEvent(movementInput=" + this.movementInput + ')';
    }

    public int hashCode() {
        return this.movementInput.hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PlayerUpdateMoveStateEvent)) {
            return false;
        }
        PlayerUpdateMoveStateEvent playerUpdateMoveStateEvent = (PlayerUpdateMoveStateEvent)other;
        if (this.movementInput==playerUpdateMoveStateEvent.movementInput) return true;
        return false;
    }
}

