package dev.cuican.staypro.mixin.entity;

import com.mojang.authlib.GameProfile;
import dev.cuican.staypro.Stay;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.event.StayEvent;
import dev.cuican.staypro.event.events.client.*;
import dev.cuican.staypro.event.events.network.*;
import dev.cuican.staypro.utils.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputUpdateEvent;

import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityPlayerSP.class}, priority=0x7FFFFFFF)
public abstract class MixinEntityPlayerSP
        extends AbstractClientPlayer {


    protected Minecraft mc = Minecraft.getMinecraft();
    @Shadow @Final
    public
    NetHandlerPlayClient connection;
    @Shadow
    private boolean serverSprintState;
    @Shadow
    private boolean serverSneakState;
    @Shadow
    private double lastReportedPosX;
    @Shadow
    private double lastReportedPosY;
    @Shadow
    private double lastReportedPosZ;
    @Shadow
    private float lastReportedYaw;
    @Shadow
    private float lastReportedPitch;
    @Shadow
    private int positionUpdateTicks;
    @Shadow
    private boolean prevOnGround;
    @Shadow
    private boolean autoJumpEnabled;
    @Shadow
    protected abstract void onUpdateWalkingPlayer();
    @Shadow public MovementInput movementInput;
    @Shadow protected abstract boolean isCurrentViewEntity();

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }
    @Inject(method = "onUpdateWalkingPlayer", at = @At(value = "HEAD"), cancellable = true)
    private void PreUpdateWalkingPlayer(CallbackInfo ci) {
        EventMotion event = new EventMotion(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
        Stay.EVENT_BUS.post(event);

        if (event.isModded()) {
            ci.cancel();
            sendMovePacket(event);
        }
    }
    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void preCheckLightFor(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At(value = "RETURN"), cancellable = true)
    private void PostUpdateWalkingPlayer(CallbackInfo ci) {
        EventMotion event = new EventMotion(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
        Stay.EVENT_BUS.post(event);
    }

    public void sendMovePacket(EventMotion event) {
        boolean flag = this.isSprinting();

        if (flag != this.serverSprintState) {
            if (flag) {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SPRINTING));
            } else {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SPRINTING));
            }

            this.serverSprintState = flag;
        }

        boolean flag1 = this.isSneaking();

        if (flag1 != this.serverSneakState) {
            if (flag1) {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SNEAKING));
            } else {
                this.connection.sendPacket(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SNEAKING));
            }

            this.serverSneakState = flag1;
        }

        if (this.isCurrentViewEntity()) {
            double d0 = event.x - this.lastReportedPosX;
            double d1 = event.y - this.lastReportedPosY;
            double d2 = event.z - this.lastReportedPosZ;
            double d3 = (double) (event.yaw - this.lastReportedYaw);
            double d4 = (double) (event.pitch - this.lastReportedPitch);
            ++this.positionUpdateTicks;
            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || this.positionUpdateTicks >= 20;
            boolean flag3 = d3 != 0.0D || d4 != 0.0D;

            if (this.isRiding()) {
                this.connection.sendPacket(new CPacketPlayer.PositionRotation(this.motionX, -999.0D, this.motionZ, event.yaw, event.pitch, event.onGround));
                flag2 = false;
            } else if (flag2 && flag3) {
                this.connection.sendPacket(new CPacketPlayer.PositionRotation(event.x, event.y, event.z, event.yaw, event.pitch, event.onGround));
            } else if (flag2) {
                this.connection.sendPacket(new CPacketPlayer.Position(event.x, event.y, event.z, event.onGround));

            } else if (flag3) {
                this.connection.sendPacket(new CPacketPlayer.Rotation(event.yaw, event.pitch, event.onGround));
            } else if (this.prevOnGround != event.onGround) {
                this.connection.sendPacket(new CPacketPlayer(event.onGround));
            }

            if (flag2) {
                this.lastReportedPosX = event.x;
                this.lastReportedPosY = event.y;
                this.lastReportedPosZ = event.z;
                this.positionUpdateTicks = 0;
            }

            if (flag3) {
                this.lastReportedYaw = event.yaw;
                this.lastReportedPitch = event.pitch;
            }

            this.prevOnGround = event.onGround;
            this.autoJumpEnabled = this.mc.gameSettings.autoJump;
        }
    }

    @Inject(method = "sendChatMessage", at = @At(value = "HEAD"), cancellable = true)
    public void sendChatPacket(String message, CallbackInfo ci) {
        ChatEvent event = new ChatEvent(message);
        Stay.EVENT_BUS.post(event);
        if (event.isCancelled()) ci.cancel();
    }



    @Inject(at = @At(value = "INVOKE",target = "Lnet/minecraft/util/MovementInput;updatePlayerMoveState()V",shift = At.Shift.AFTER),method = "onLivingUpdate")
    private void onLivingUpdate(CallbackInfo ci){
    new InputUpdateEvent(this,this.movementInput);
        PlayerInteractEvent event = new PlayerInteractEvent(this,this.movementInput);
        Stay.EVENT_BUS.post(event);

    }

    @Inject(method={"onUpdateWalkingPlayer"}, at=@At(value="HEAD"), cancellable=true)
    private void preMotion(CallbackInfo ci) {
        UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(0);
        Stay.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            return;
        }
        ci.cancel();
    }

    @Inject(method="onUpdateWalkingPlayer", at=@At(value="RETURN"), cancellable=true)
    private void postMotion(CallbackInfo ci) {
        UpdateWalkingPlayerEvent event = new UpdateWalkingPlayerEvent(1);
        Stay.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            return;
        }
        ci.cancel();
    }

    @Inject(method={"move"}, at=@At(value="HEAD"), cancellable=true)
    public void move(MoverType moverType, double n, double n2, double n3, CallbackInfo ci) {
        if(mc==null)return;
        MoveEvent2 event = new MoveEvent2( moverType, n, n2, n3);
        Stay.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            return;
        }
        super.move(moverType, event.getX(), event.getY(), event.getZ());
        ci.cancel();
    }


    @Inject(method={"onUpdate"}, at=@At(value="INVOKE", target="net/minecraft/client/entity/EntityPlayerSP.onUpdateWalkingPlayer()V", ordinal=0, shift=At.Shift.AFTER))
    private void onMotionPostUpdateFactor(CallbackInfo ci) {
        MotionUpdateMultiplierEvent event = new MotionUpdateMultiplierEvent();
        Stay.EVENT_BUS.post(event);
        int factorIn = event.getFactor() - 1;
        for (int i = 0; i < factorIn; ++i) {
            EntityPlayerSP local = mc.player;
            int cacheSinceLastSwing = this.ticksSinceLastSwing;
            int cacheActiveItemStackUseCount = this.activeItemStackUseCount;
            int cacheHurtTime = local.hurtTime;
            float cachePrevSwingProgress = local.prevSwingProgress;
            float cacheSwingProgress = local.swingProgress;
            int cacheSwingProgressInt = local.swingProgressInt;
            boolean cacheIsSwingInProgress = local.isSwingInProgress;
            float cacheRotationYaw = local.rotationYaw;
            float cachePrevRotationYaw = local.prevRotationYaw;
            float cacheRenderYawOffset = local.renderYawOffset;
            float cachePrevRenderYawOffset = local.prevRenderYawOffset;
            float cacheRotationYawHead = local.rotationYawHead;
            float cachePrevRotationYawHead = local.prevRotationYawHead;
            float cacheCameraYaw = local.cameraYaw;
            float cachePrevCameraYaw = local.prevCameraYaw;
            float cacheRenderArmYaw = local.renderArmYaw;
            float cachePrevRenderArmYaw = local.prevRenderArmYaw;
            float cacheRenderArmPitch = local.renderArmPitch;
            float cachePrevRenderArmPitch = local.prevRenderArmPitch;
            float cacheDistanceWalkedModified = local.distanceWalkedModified;
            float cachePrevDistanceWalkedModified = local.prevDistanceWalkedModified;
            double cacheChasingPosX = local.chasingPosX;
            double cachePrevChasingPosX = local.prevChasingPosX;
            double cacheChasingPosY = local.chasingPosY;
            double cachePrevChasingPosY = local.prevChasingPosY;
            double cacheChasingPosZ = local.chasingPosZ;
            double cachePrevChasingPosZ = local.prevChasingPosZ;
            float cacheLimbSwingAmount = local.limbSwingAmount;
            float cachePrevLimbSwingAmount = local.prevLimbSwingAmount;
            float cacheLimbSwing = local.limbSwing;
            super.onUpdate();
            this.ticksSinceLastSwing = cacheSinceLastSwing;
            this.activeItemStackUseCount = cacheActiveItemStackUseCount;
            local.hurtTime = cacheHurtTime;
            local.prevSwingProgress = cachePrevSwingProgress;
            local.swingProgress = cacheSwingProgress;
            local.swingProgressInt = cacheSwingProgressInt;
            local.isSwingInProgress = cacheIsSwingInProgress;
            local.rotationYaw = cacheRotationYaw;
            local.prevRotationYaw = cachePrevRotationYaw;
            local.renderYawOffset = cacheRenderYawOffset;
            local.prevRenderYawOffset = cachePrevRenderYawOffset;
            local.rotationYawHead = cacheRotationYawHead;
            local.prevRotationYawHead = cachePrevRotationYawHead;
            local.cameraYaw = cacheCameraYaw;
            local.prevCameraYaw = cachePrevCameraYaw;
            local.renderArmYaw = cacheRenderArmYaw;
            local.prevRenderArmYaw = cachePrevRenderArmYaw;
            local.renderArmPitch = cacheRenderArmPitch;
            local.prevRenderArmPitch = cachePrevRenderArmPitch;
            local.distanceWalkedModified = cacheDistanceWalkedModified;
            local.prevDistanceWalkedModified = cachePrevDistanceWalkedModified;
            local.chasingPosX = cacheChasingPosX;
            local.prevChasingPosX = cachePrevChasingPosX;
            local.chasingPosY = cacheChasingPosY;
            local.prevChasingPosY = cachePrevChasingPosY;
            local.chasingPosZ = cacheChasingPosZ;
            local.prevChasingPosZ = cachePrevChasingPosZ;
            local.limbSwingAmount = cacheLimbSwingAmount;
            local.prevLimbSwingAmount = cachePrevLimbSwingAmount;
            local.limbSwing = cacheLimbSwing;
            this.onUpdateWalkingPlayer();
        }
        int i2 = 0;
        while (i2 < event.getFactor() - 1 - factorIn) {
            this.onUpdateWalkingPlayer();
            ++i2;
        }
    }

    @Inject(method={"onLivingUpdate"}, at=@At(value="INVOKE", target="Lnet/minecraft/util/MovementInput;updatePlayerMoveState()V"))
    private void onMoveStateUpdate(CallbackInfo ci) {
        Stay.EVENT_BUS.post(new PlayerUpdateMoveStateEvent(this.movementInput));
    }
    private double cachedX;
    private double cachedY;
    private double cachedZ;
    private float cachedRotationPitch;
    private float cachedRotationYaw;
    private boolean cachedMoving;
    private boolean cachedOnGround;

    @Inject(method={"onUpdateWalkingPlayer"}, at=@At(value="HEAD"), cancellable=true)
    private void onUpdateWalkingPlayerPre(CallbackInfo ci) {
        mc.profiler.startSection("muffinPreMotionUpdate");
        this.cachedX = this.posX;
        this.cachedY = this.posY;
        this.cachedZ = this.posZ;
        this.cachedRotationYaw = this.rotationYaw;
        this.cachedRotationPitch = this.rotationPitch;
        this.cachedOnGround = this.onGround;
        Location location = new Location(this.posX, this.posY, this.posZ, this.onGround, Location.isMoving());
        Vec2f prevRotation = new Vec2f(this.prevRotationYaw, this.prevRotationPitch);
        Vec2f rotation = new Vec2f(this.rotationYaw, this.rotationPitch);
        MotionUpdateEvent motionEvent = new MotionUpdateEvent( location, rotation, prevRotation);
        Stay.EVENT_BUS.post(motionEvent);
        this.posX = motionEvent.getLocation().getX();
        this.posY = motionEvent.getLocation().getY();
        this.posZ = motionEvent.getLocation().getZ();
        this.rotationYaw = motionEvent.getRotation().x;
        this.rotationPitch = motionEvent.getRotation().y;
        this.onGround = motionEvent.getLocation().isOnGround();
        if (motionEvent.getRotating()) {
            this.rotationYawHead = motionEvent.getRotation().x;
            this.renderYawOffset = motionEvent.getRotation().x;
        }
        if (motionEvent.isCanceled()) {
            ci.cancel();
        }
        mc.profiler.endSection();
    }

    private MotionUpdateEvents.Riding riding;
    private MotionUpdateEvents motionEvent;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = "onUpdate",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/network/play/client/CPacketPlayer$Rotation",
                    shift = At.Shift.BEFORE),
            cancellable = true)
    private void ridingHook_1(CallbackInfo info)
    {
        this.riding = new MotionUpdateEvents.Riding(
                this.posX,
                this.getEntityBoundingBox().minY,
                this.posZ,
                this.rotationYaw,
                this.rotationPitch,
                this.onGround,
                this.moveStrafing,
                this.moveForward,
                this.movementInput.jump,
                this.movementInput.sneak);

        Stay.EVENT_BUS.post(this.riding);
        if (this.riding.isCancelled())
        {
            info.cancel();
        }
    }
    @Inject(
            method = "onUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/NetHandlerPlayClient;sendPacket(Lnet/minecraft/network/Packet;)V",
                    ordinal = 2,
                    shift = At.Shift.BY,
                    by = 2)) // Inject after the If-Statement
    private void ridingHook_9(CallbackInfo info)
    {
        Stay.EVENT_BUS.post(new MotionUpdateEvents.Riding( riding));
    }
    @Inject(
            method = "onUpdateWalkingPlayer",
            at = @At(
                    value = "HEAD"),
            cancellable = true)
    private void onUpdateWalkingPlayer_Head(CallbackInfo callbackInfo)
    {
        motionEvent = new MotionUpdateEvents(
                this.posX,
                this.getEntityBoundingBox().minY,
                this.posZ,
                this.rotationYaw,
                this.rotationPitch,
                this.onGround);
        Stay.EVENT_BUS.post(motionEvent);
        if (motionEvent.isCancelled())
        {
            callbackInfo.cancel();
        }
    }
}
