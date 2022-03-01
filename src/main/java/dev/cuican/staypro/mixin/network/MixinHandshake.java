package dev.cuican.staypro.mixin.network;


import dev.cuican.staypro.Stay;
import dev.cuican.staypro.event.events.client.ClientDisconnectionFromServerEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = NetworkDispatcher.class)
public class MixinHandshake {


    @Inject(method = {"disconnect(Lio/netty/channel/ChannelHandlerContext;Lio/netty/channel/ChannelPromise;)V"}, at = @At("HEAD"),remap = false )
    private void ondisconnect(ChannelHandlerContext ctx, ChannelPromise promise, CallbackInfo callbackInfo) {
        ClientDisconnectionFromServerEvent event = new ClientDisconnectionFromServerEvent();
        Stay.EVENT_BUS.post(event);
    }
}
