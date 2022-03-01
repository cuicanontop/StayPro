/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 */
package dev.cuican.staypro.mixin.entity;



import dev.cuican.staypro.Stay;
import dev.cuican.staypro.event.events.client.LandStepEvent;
import dev.cuican.staypro.event.events.client.MoveEvent;
import dev.cuican.staypro.event.events.client.PushEvent;
import dev.cuican.staypro.event.events.client.StepEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Entity.class}, priority=998)
public abstract class MixinEntity {
    @Redirect(method={"applyEntityCollision"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;addVelocity(DDD)V",ordinal = 0))
    public void addVelocityHook(Entity entity, double x, double y, double z) {
        PushEvent event = new PushEvent( entity, x, y, z, true);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return;
        }
        entity.motionX += event.x;
        entity.motionY += event.y;
        entity.motionZ += event.z;
        entity.isAirBorne = event.airbone;
    }
    @Shadow
    public void move(MoverType type, double x, double y, double z) {

    }
    @Inject(method={"move"}, at=@At(value="HEAD"))
    public void onMovePre(MoverType type, double x, double y, double z, CallbackInfo ci) {
        Entity entity = mc.player;
        if (!(entity instanceof EntityPlayerSP)) return;
        this.moveEvent = new MoveEvent(type, x, y, z, entity.isSneaking());
        Stay.EVENT_BUS.post(moveEvent);
    }


    @ModifyVariable(method={"move"}, at=@At(value="HEAD"), ordinal=0)
    private double onMoveX(double x) {
        if (this.moveEvent == null) return x;
        return this.moveEvent.getX();
    }

    @ModifyVariable(method={"move"}, at=@At(value="HEAD"), ordinal=1)
    private double onMoveY(double y) {
        if (this.moveEvent == null) return y;
        return this.moveEvent.getY();
    }

    @ModifyVariable(method={"move"}, at=@At(value="HEAD"), ordinal=2)
    private double onMoveZ(double z) {
        if (this.moveEvent == null) return z;
        return this.moveEvent.getZ();
    }

    @Redirect(method={"move"}, at=@At(value="INVOKE", target="net/minecraft/entity/Entity.isSneaking()Z", ordinal=0))
    private boolean onMoveSneaking(Entity entity) {
        boolean bl;
        if (this.moveEvent != null) {
            bl = this.moveEvent.isSneaking();
            return bl;
        }
        bl = entity.isSneaking();
        return bl;
    }

    @Inject(method={"move"}, at=@At(value="RETURN"))
    public void onMovePost(MoverType type, double x, double y, double z, CallbackInfo ci) {
        this.moveEvent = null;
    }

    @Inject(method={"move"}, at=@At(value="FIELD", target="Lnet/minecraft/entity/Entity;stepHeight:F", shift=At.Shift.BEFORE, ordinal=3))
    private void onInjectStepPre(MoverType type, double x, double y, double z, CallbackInfo ci) {

        this.cachedStepHeight = this.stepHeight;
        this.stepEvent = new StepEvent( this.stepHeight);
        Stay.EVENT_BUS.post(this.stepEvent);
    }
    @Shadow
    public float stepHeight;
    private StepEvent stepEvent;
    private float cachedStepHeight;
    @Inject(method={"move"}, at=@At(value="FIELD", target="Lnet/minecraft/entity/Entity;stepHeight:F", ordinal=4, shift=At.Shift.BEFORE), require=1)
    private void onInjectStepPre(CallbackInfo ci) {
        this.stepHeight = this.stepEvent.getHeight();
    }

    @Inject(method={"move"}, at=@At(value="INVOKE", target="Lnet/minecraft/profiler/Profiler;endSection()V", shift=At.Shift.BEFORE, ordinal=0))
    private void onInjectStepEventPost(CallbackInfo ci) {
        this.stepHeight = this.cachedStepHeight;
        Stay.EVENT_BUS.post(new StepEvent(this.stepHeight));
    }
    @Inject(method={"move"}, at=@At(value="FIELD", target="net/minecraft/entity/Entity.onGround:Z", ordinal=1))
    private void onStepGround(MoverType type, double x, double y, double z, CallbackInfo ci) {
        Entity entity = mc.player;
        if (!(entity instanceof EntityPlayerSP)) return;
        LandStepEvent event = new LandStepEvent(this.getEntityBoundingBox(), entity.stepHeight);
        Stay.EVENT_BUS.post(event);
        entity.stepHeight = event.getStepHeight();
    }
    protected Minecraft mc = Minecraft.getMinecraft();
    private MoveEvent moveEvent;
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;
    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();
}

