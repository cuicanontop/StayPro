package dev.cuican.staypro.mixin.render;

import dev.cuican.staypro.Stay;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.event.decentraliized.DecentralizedRenderWorldEvent;
import dev.cuican.staypro.event.events.render.HudOverlayEvent;
import dev.cuican.staypro.event.events.render.RenderLiquidVisionEvent;
import dev.cuican.staypro.event.events.render.RenderTotemPopEvent;
import dev.cuican.staypro.event.events.render.RenderWorldEvent;
import dev.cuican.staypro.module.modules.render.CameraClip;
import dev.cuican.staypro.module.modules.render.NoRender;
import dev.cuican.staypro.notification.NotificationManager;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
    @Shadow
    @Final
    public Minecraft mc;
    /**
     * Mixin have bugs,sometimes we may inject failed,so we use ASM
     *
     * @club.eridani.cursa.asm.impl.PatchEntityRenderer
     */
    //@Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiIngame.renderGameOverlay(F)V"))
    //public void updateCameraAndRender$renderGameOverlay(float partialTicks, long nanoTime, CallbackInfo ci) {
    //    Stay.EVENT_BUS.post(new RenderOverlayEvent(partialTicks));
    //}
    @Shadow
    protected abstract void setupCameraTransform(float partialTicks, int pass);

    public void runSetupCameraTransform(float partialTicks, int pass) {
        this.setupCameraTransform(partialTicks, pass);
    }

    @Inject(
            method = {"updateCameraAndRender"},
            at = {@At("RETURN")}
    )
    public void updateCameraAndRender$Inject$RETURN(float partialTicks, long nanoTime, CallbackInfo ci) {
        NotificationManager.draw();
    }
    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void hurtCameraEffect(float partialTicks, CallbackInfo ci) {
        HudOverlayEvent event = new HudOverlayEvent(HudOverlayEvent.Type.HURTCAM);
        Stay.EVENT_BUS.post(event);
        if (event.isCancelled())
            ci.cancel();
    }
    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void shurtCameraEffect(float ticks, CallbackInfo info) {
        if(ModuleManager.getModuleByName("NoRender").isEnabled() && ((NoRender) ModuleManager.getModuleByName("NoRender")).hurtCam.getValue()){
            info.cancel();
        }

     }
    @ModifyVariable(method={"orientCamera"}, ordinal=3, at=@At(value="STORE", ordinal=0), require=1)
    public double changeCameraDistanceHook(double range) {
        if (CameraClip.INSTANCE.isEnabled()) {
            return CameraClip.INSTANCE.distance.getValue();
        } else {
            return range;
        }
    }

    @ModifyVariable(method={"orientCamera"}, ordinal=7, at=@At(value="STORE", ordinal=0), require=1)
    public double orientCameraHook(double range) {
        if (CameraClip.INSTANCE.isEnabled()) {
            return CameraClip.INSTANCE.distance.getValue();
        } else {
            return range;
        }
    }


    @Inject(method = "displayItemActivation", at = @At(value = "HEAD"), cancellable = true)
    public void onDisplayItemActivationPre(ItemStack stack, CallbackInfo ci) {
        RenderTotemPopEvent event = new RenderTotemPopEvent();
        Stay.EVENT_BUS.post(event);
        if (event.isCanceled()) ci.cancel();
    }
    @Redirect(method = {"setupFog"}, at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/ActiveRenderInfo.getBlockStateAtEntityViewpoint(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/block/state/IBlockState;"))
    public IBlockState onSettingUpFogWhileInLiquid(World worldIn, Entity entityIn, float p_186703_2_) {
        IBlockState iBlockState = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entityIn, p_186703_2_);
        RenderLiquidVisionEvent event = new RenderLiquidVisionEvent();
        Stay.EVENT_BUS.post(event);
        if (event.isCanceled() && (iBlockState.getMaterial() == Material.LAVA || iBlockState.getMaterial() == Material.WATER)) {
            return Blocks.AIR.getDefaultState();
        }
        return iBlockState;
    }
    @Inject(method = "renderItemActivation", at = @At(value = "HEAD"), cancellable = true)
    public void onRenderItemActivationPre(int p_190563_1_, int p_190563_2_, float p_190563_3_, CallbackInfo ci) {
        RenderTotemPopEvent event = new RenderTotemPopEvent();
        Stay.EVENT_BUS.post(event);
        if (event.isCanceled()) ci.cancel();
    }
    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE_STRING", target = "net/minecraft/profiler/Profiler.endStartSection(Ljava/lang/String;)V", args = "ldc=hand"))
    public void onStartHand(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        RenderWorldEvent event = new RenderWorldEvent(partialTicks, pass);
        DecentralizedRenderWorldEvent.instance.post(event);
        Stay.EVENT_BUS.post(event);
    }

}
