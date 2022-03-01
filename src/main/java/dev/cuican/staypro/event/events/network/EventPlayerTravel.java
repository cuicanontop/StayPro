package dev.cuican.staypro.event.events.network;


import dev.cuican.staypro.event.StayEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class EventPlayerTravel extends StayEvent {
    
    public float Strafe;
    public float Vertical;
    public float Forward;

    public EventPlayerTravel(float p_Strafe, float p_Vertical, float p_Forward) {
        Strafe = p_Strafe;
        Vertical = p_Vertical;
        Forward = p_Forward;
    }

}