package dev.cuican.staypro.mixin.accessor;


import net.minecraft.network.play.client.CPacketCloseWindow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CPacketCloseWindow.class)
public interface AccessorClient {

    @Accessor("windowId")
    int aqGetWindowId();

}
