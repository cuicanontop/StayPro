package dev.cuican.staypro.event.events.network;

import dev.cuican.staypro.event.StayEvent;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;

public class ClientChatReceivedEvent extends StayEvent {
    private ITextComponent message;
    private final ChatType type;
    public ClientChatReceivedEvent(ChatType type, ITextComponent message)
    {
        this.type = type;
        this.setMessage(message);
    }

    public ITextComponent getMessage()
    {
        return message;
    }

    public void setMessage(ITextComponent message)
    {
        this.message = message;
    }

    public ChatType getType()
    {
        return type;
    }
}
