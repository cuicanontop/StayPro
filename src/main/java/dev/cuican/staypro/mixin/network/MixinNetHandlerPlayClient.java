package dev.cuican.staypro.mixin.network;

import dev.cuican.staypro.Stay;
import dev.cuican.staypro.event.events.network.ClientChatReceivedEvent;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
    @Inject(method = {"handleChat(Lnet/minecraft/network/play/server/SPacketChat;)V"}, at = @At("INVOKE") )
    private void onClientChat(SPacketChat packetIn, CallbackInfo callbackInfo) {
        ClientChatReceivedEvent event = new ClientChatReceivedEvent(packetIn.getType(), packetIn.getChatComponent());
        Stay.EVENT_BUS.post(event);
        if (event.isCancelled())
            callbackInfo.cancel();
    }
}
