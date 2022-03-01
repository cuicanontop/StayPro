package dev.cuican.staypro.mixin.render;

import dev.cuican.staypro.Stay;
import dev.cuican.staypro.event.decentraliized.DecentralizedRenderTickEvent;
import dev.cuican.staypro.event.events.render.RenderOverlayEvent;
import net.minecraft.client.gui.GuiSubtitleOverlay;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiSubtitleOverlay.class)
public class MixinGuiSubtitleOverlay {

    @Inject(method = "renderSubtitles", at = @At("HEAD"))
    public void onRender2D(ScaledResolution resolution, CallbackInfo ci) {
        RenderOverlayEvent event = new RenderOverlayEvent();
        DecentralizedRenderTickEvent.instance.post(event);
        Stay.EVENT_BUS.post(event);
    }

}
