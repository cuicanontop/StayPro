package dev.cuican.staypro.event.events.client;

import dev.cuican.staypro.event.StayEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;

public class PlayerInteractEvent  extends StayEvent {
    private final MovementInput MovementInput;

    public PlayerInteractEvent(EntityPlayer Player, MovementInput movementInput) {
        MovementInput = movementInput;

    }

   public MovementInput getMovementInput(){
        return MovementInput;
   }


}
