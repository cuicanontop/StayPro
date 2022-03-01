package dev.cuican.staypro.mixin.render;


import dev.cuican.staypro.Stay;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.event.events.render.RenderBlockOverlayEvent;
import dev.cuican.staypro.event.events.render.RenderItemAnimationEvent;
import dev.cuican.staypro.event.events.render.RenderOverlayEvent2;
import dev.cuican.staypro.event.events.render.TransformSideFirstPersonEvent;
import dev.cuican.staypro.module.modules.render.ViewModel;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {

    @Inject(method = {"renderWaterOverlayTexture"}, at = @At(value = "HEAD"), cancellable = true)
    public void preRenderWaterOverlayTexture(float partialTicks, CallbackInfo ci) {
        RenderOverlayEvent2 event = new RenderOverlayEvent2(RenderOverlayEvent2.OverlayType.LIQUID);
        Stay.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderOverlays", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/renderer/ItemRenderer;renderFireInFirstPerson()V",shift = At.Shift.AFTER), cancellable = true)
    public void prerenderOverlays(float partialTicks, CallbackInfo ci) {
        RenderBlockOverlayEvent event = new RenderBlockOverlayEvent(partialTicks);
        Stay.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = {"renderFireInFirstPerson"}, at = @At(value = "HEAD"), cancellable = true)
    public void preRenderFireInFirstPerson(CallbackInfo ci) {
        RenderOverlayEvent2 event = new RenderOverlayEvent2(RenderOverlayEvent2.OverlayType.FIRE);
        Stay.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = {"renderSuffocationOverlay"}, at = @At(value = "HEAD"), cancellable = true)
    private void onRenderSuffocationOverlay(TextureAtlasSprite sprite, CallbackInfo ci) {
        RenderOverlayEvent2 event = new RenderOverlayEvent2(RenderOverlayEvent2.OverlayType.BLOCK);
        Stay.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "transformSideFirstPerson", at = @At("HEAD"))
    public void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_, CallbackInfo callbackInfo) {
        TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
        Stay.EVENT_BUS.post(event);
    }

    @Inject(method = "transformEatFirstPerson", at = @At("HEAD"), cancellable = true)
    public void transformEatFirstPerson(float p_187454_1_, EnumHandSide hand, ItemStack stack, CallbackInfo callbackInfo) {
        TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
        Stay.EVENT_BUS.post(event);

        if (ModuleManager.getModuleByName("ViewModel").isEnabled() && ((ViewModel) ModuleManager.getModuleByName("ViewModel")).cancelEating.getValue()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At("HEAD"), cancellable = true)
    private void onRenderItemAnimationPre(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo ci) {
        RenderItemAnimationEvent.Render uwu = new RenderItemAnimationEvent.Render(stack, hand);
        if (uwu.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemSide(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V"))
    private void onRenderItemTransformAnimationPre(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo info) {
        Stay.EVENT_BUS.post(new RenderItemAnimationEvent.Transform(stack, hand, p_187457_5_));
    }
}