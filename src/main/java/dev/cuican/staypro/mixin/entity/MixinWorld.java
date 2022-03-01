package dev.cuican.staypro.mixin.entity;

import dev.cuican.staypro.Stay;
import dev.cuican.staypro.event.events.network.EntityJoinWorldEvent;
import dev.cuican.staypro.event.events.render.RenderLightEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;


@Mixin(value = {World.class})
public abstract class MixinWorld {

    @Inject(at = @At(value = "HEAD",target = "Lnet/minecraft/world/World;onEntityAdded(Lnet/minecraft/entity/Entity;)V"),method = "loadEntities")
    private void EntityJoinWorldEventS(Collection<Entity> entityCollection,CallbackInfo ci){
        EntityJoinWorldEvent event = new EntityJoinWorldEvent(0,entityCollection);
        Stay.EVENT_BUS.post(event);

    }
    @Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
    private void checkLightForHead(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> ci) {
        RenderLightEvent event = new RenderLightEvent();
        Stay.EVENT_BUS.post(event);
        if (event.isCanceled() && lightType == EnumSkyBlock.SKY) {
            ci.setReturnValue(false);
        }
    }

}
