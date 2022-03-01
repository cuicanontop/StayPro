package dev.cuican.staypro.module.modules.combat;

import com.google.common.collect.Sets;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.StayEvent;
import dev.cuican.staypro.event.events.client.LandStepEvent;
import dev.cuican.staypro.event.events.client.MoveEvent;
import dev.cuican.staypro.event.events.client.StepEvent;
import dev.cuican.staypro.event.events.network.MotionUpdateEvent;
import dev.cuican.staypro.event.events.network.MotionUpdateMultiplierEvent;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.event.events.network.PlayerUpdateMoveStateEvent;
import dev.cuican.staypro.event.events.render.RenderEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.*;
import dev.cuican.staypro.utils.Timer;
import dev.cuican.staypro.utils.graphics.RenderUtils3D;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.stats.StatList;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.*;
import org.lwjgl.opengl.GL11;
import scala.Unit;

import java.awt.*;
import java.util.*;
import java.util.List;


@ModuleInfo(name = "HoleSnap", description = "HoleSnap", category = Category.COMBAT)
public class HoleSnap extends Module {
    private final Setting<Boolean> airStrafe = setting("AirStafe", false);
    public final Setting<Float> range = setting("Range", 4.0f,  1f, 8.0f);
    public final Setting<Integer> dragTicks = setting("dragTicks", 3, 1, 5);
    private final Setting<Boolean> disableStrafe = setting("noSpeed", false);
    private final Setting<Boolean> Openafterclosing = setting("SpeedOpenafterclosing", false).whenTrue(disableStrafe);
    private final Setting<Boolean> ReverseStep = setting("noReverseStep", false);
    private final Setting<Boolean> OpenafterclosingReverseStep = setting("ReverseStepOpenafterclosing", false).whenTrue(ReverseStep);

    private final Setting<Boolean> Step = setting("Step", false);
    public final Setting<Float> height = setting("StepHeight", 2.5f,  1f, 4.0f).whenTrue(Step);
    public static BlockPos.MutableBlockPos holePos = new BlockPos.MutableBlockPos(0, -69, 0);
    private static int stuckTicks;
    private static int stuckTicks2;
    private static boolean canDrag;
    private static final Timer newTimer = new Timer();
    private static int fix;
    private static double oldY;
    private static int ticksCount;
    private static float timerSpeed;
    private static final BlockPos[] surroundOffset  = new BlockPos[]{new BlockPos(0, -1, 0), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(-1, 0, 0)};;
    private static Vec2f serverSideRotation = Vec2f.ZERO;
    private static final double[] fourStep = {0.42,0.75,0.63,0.51,0.9,1.21,1.45,1.43,1.78,1.63,1.51,1.9,2.21,2.45,2.43,2.78,2.63,2.51,2.9,3.21,3.45,3.43};
    private static final double[] threeStep = {0.42,0.78,0.63,0.51,0.9,1.21,1.45,1.43,1.78,1.63,1.51,1.9,2.21,2.45,2.43};
    private static final double[] twoHalfStep={0.425,0.821,0.699,0.599,1.022,1.372,1.652,1.869,2.019,1.907};
    private static final double[] twoStep={0.42,0.78,0.63,0.51,0.9,1.21,1.45,1.43};
    private static final double[] oneHalfStep={0.41999998688698,0.7531999805212,1.00133597911214,1.16610926093821,1.24918707874468,1.1707870772188};
    private static final double[] oneStep = {0.41999998688698,0.7531999805212};
    private static final double[] halfStep={0.39,0.6938};
    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!(event.packet instanceof SPacketPlayerPosLook)) return;

        float yaw = ((SPacketPlayerPosLook)event.packet).getYaw();
        float pitch = ((SPacketPlayerPosLook)event.packet).getPitch();
        if (((SPacketPlayerPosLook)event.packet).getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y_ROT)) {
            yaw += mc.player.rotationYaw;
        }
        if (((SPacketPlayerPosLook)event.packet).getFlags().contains(SPacketPlayerPosLook.EnumFlags.X_ROT)) {
            pitch += mc.player.rotationPitch;
        }
        serverSideRotation = new Vec2f(yaw, pitch);
        this.disable();
    }
    public final Vec2f getRotationTo( Vec3d posFrom, Vec3d posTo) {

        Vec3d vec3d = posTo.subtract(posFrom);
        return this.getRotationFromVec(vec3d);
    }
    public static final double toDegree(double $this$toDegree) {
        return $this$toDegree * 180.0 / Math.PI;
    }
    public boolean disableStrafe1 = false;
    public boolean OpenafterclosingReverseStep2 = false;
    public final double normalizeAngle(double angleIn) {
        double angle = angleIn;
        if ((angle %= 360.0) >= 180.0) {
            angle -= 360.0;
        }
        if (!(angle < -180.0)) return angle;
        angle += 360.0;
        return angle;
    }

    public final Vec2f getRotationFromVec( Vec3d vec) {
        double d = vec.x;
        double d2 = vec.z;
        double xz = Math.hypot(d, d2);
        d2 = vec.z;
        double d3 = vec.x;
        double yaw =toDegree(Math.atan2(d2, d3)) - 90.0;
        d3 = vec.y;
        double pitch = -toDegree(Math.atan2(d3, xz));
        double yawDiff = yaw - (double)serverSideRotation.x ;
        if (!(yawDiff < -180.0)) {
            if (!(yawDiff > 180.0)) return new Vec2f((float) normalizeAngle(serverSideRotation.x+ yawDiff),(float) normalizeAngle(pitch));
        }
        double d4 = yawDiff / (double)360.0f;
        d4 = Math.abs(d4);

        double round = Math.rint(d4);

        yawDiff = yawDiff < 0.0 ? yawDiff + (double)360.0f * round : yawDiff - (double)360.0f * round;
        return new Vec2f((float) normalizeAngle(serverSideRotation.x+ yawDiff),(float) normalizeAngle(pitch));
    }
    public static double[] forward(final double n) {
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float n2 = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                n2 += ((moveForward > 0.0f) ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                n2 += ((moveForward > 0.0f) ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(n2 + 90.0f));
        final double cos = Math.cos(Math.toRadians(n2 + 90.0f));
        return new double[]{moveForward * n * cos + moveStrafe * n * sin, moveForward * n * sin - moveStrafe * n * cos};
    }
    private final boolean canStep() {
        if (MovementUtils.isMoving()) {
            if (mc.player.onGround) return true;
        }
        if (!mc.player.isOnLadder()) return true;
        if (!mc.player.isInWater()) return true;
        if (mc.player.isInLava()) return false;
        if (EntityUtil.isInWater(mc.player)) return false;
        if (!mc.player.collidedHorizontally) return false;
        if (!mc.player.collidedVertically) return false;
        if (mc.player.movementInput.jump) return false;
        if (!((double)mc.player.fallDistance < 0.1)) return false;
        return true;
    }
    @Listener
    public void onRenderWorld(RenderEvent event) {
        if(holePos==null)return;
        Vec3d posFrom = EntityUtil.getInterpolatedPos(mc.player,mc.isGamePaused ? mc.renderPartialTicksPaused : mc.getRenderPartialTicks());
        Color color = new Colors(32, 255, 32, 255);
        RenderUtils3D.putVertex(posFrom.x, posFrom.y, posFrom.z, color);
        RenderUtils3D.putVertex(holePos.getX()+0.5, holePos.getY()+0.5, holePos.getZ()+0.5, color);
    }
    @Listener
    private final void onStepLanding(LandStepEvent event) {
        if(!Step.getValue())return;
        float f;
        Entity entity = mc.player.getRidingEntity();
        if (entity == null) {
            return;
        }
        Entity entity2 = entity;
        Entity it = entity2;
        f =  height.getValue();
        it.stepHeight = f;
    }
    @Listener
    private final void onMotionUpdate(MotionUpdateEvent event) {
        if(!Step.getValue())return;
        int n;
        if (event.getLocation().getY() - event.getLocation().getYaw() >= 0.75) {
            List<AxisAlignedBB> axisAlignedBBS = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().expand(0.0, -0.1, 0.0));
            if (!axisAlignedBBS.isEmpty()) {
                newTimer.reset();
            }
        }
        if (fix <= 0) return;
        n = fix;
        fix = n + -1;
    }
    @Listener
    private void onStep(StepEvent event) {
        if (fullNullCheck()) return;
        if(!Step.getValue())return;
        if (!this.canStep()) {
            return;
        }
        if (fix != 0) return;
        oldY = mc.player.posY;
        event.setHeight(height.getValue());
        double offset = mc.player.getEntityBoundingBox().minY - oldY;
        if (!(offset > 0.6)) return;
        if (fix != 0) return;
        if (!this.canStep()) return;
        if (!newTimer.passed(65.0)) return;
        this.fakeJump();
        timerSpeed = offset > 1.0 ? 0.15f : 0.35f;
        Double d = Double.valueOf(height.getValue());
        if (d >= 4.0 && offset == 4.0) {
            for ( double yPos : fourStep) {
              mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + yPos, mc.player.posZ, mc.player.onGround));
            }
            mc.player.setPosition(mc.player.posX, mc.player.posY + 4.0,mc.player.posZ);
        }
        Double d2 = ( (double)height.getValue());
        if (d2 >= 3.0 && offset == 3.0) {
            for (double yPos : threeStep) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + yPos, mc.player.posZ, mc.player.onGround));
            }
            mc.player.setPosition(mc.player.posX, mc.player.posY + 3.0, mc.player.posZ);

        }
        Double d3 =  Double.valueOf(height.getValue());
        if (d3 >= 2.5 && offset == 2.5) {
            for (double yPos : twoHalfStep) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + yPos, mc.player.posZ, mc.player.onGround));
            }
            mc.player.setPosition(mc.player.posX, mc.player.posY + 2.5, mc.player.posZ);

        }
        Double d4 =  Double.valueOf(height.getValue());
        if (d4 >= 2.0 && offset == 2.0) {
            for (double yPos : twoStep) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + yPos, mc.player.posZ, mc.player.onGround));
            }
            mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ);

        }
        Double d5 =  Double.valueOf(height.getValue());
        if (d5 >= 1.5 && offset == 1.5) {
            for (double yPos : oneHalfStep) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + yPos, mc.player.posZ, mc.player.onGround));
            }
            mc.player.setPosition(mc.player.posX, mc.player.posY + 1.5, mc.player.posZ);

        }
        Double d6 =  Double.valueOf(height.getValue());
        if (d6 >= 1.0 && offset == 1.0) {
            for (double yPos : oneStep) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + yPos, mc.player.posZ, mc.player.onGround));
            }
            mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0, mc.player.posZ);

        }
        Double d7 =  Double.valueOf(height.getValue());
        if (d7 >= 0.5 && offset == 0.6) {
            for (double yPos : halfStep) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + yPos, mc.player.posZ, mc.player.onGround));
            }
            mc.player.setPosition(mc.player.posX, mc.player.posY + 0.6, mc.player.posZ);

        }

        fix = 2;
    }
    private void fakeJump() {
       mc.player.isAirBorne = true;
        mc.player.addStat(StatList.JUMP, 1);
    }


    @Listener
    private void onPlayerUpdateMoveState(PlayerUpdateMoveStateEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (!(event.getMovementInput() instanceof MovementInputFromOptions)) return;
        if(holePos!=null )if (holePos.getY() == -69) return;
        MovementUtils.resetMove(event.getMovementInput());
    }

    public void onDisable(){
        if (fullNullCheck()) {
            return;
        }
        holePos  = new BlockPos.MutableBlockPos(0, -69, 0);
        if(Step.getValue()){
            mc.player.stepHeight = 0.5f;
        }
        if(OpenafterclosingReverseStep2){
            ModuleManager.getModuleByName("ReverseStep").enable();
        }
        if(disableStrafe1){
            ModuleManager.getModuleByName("Speed").enable();
        }
        stuckTicks = 0;
        stuckTicks2 = 0;
        canDrag =false;
        OpenafterclosingReverseStep2 = false;
        disableStrafe1 = false;
    }
    public final boolean isCentered(Entity isCentered, BlockPos pos) {
        double d = (double)pos.getX() + 0.31;
        double d2 = (double)pos.getX() + 0.69;
        double d3 = isCentered.posX;
        if (!(d <= d3)) return false;
        if (!(d3 <= d2)) return false;
        boolean bl = true;
        if (!bl) return false;
        d = (double)pos.getZ() + 0.31;
        d2 = (double)pos.getZ() + 0.69;
        d3 = isCentered.posZ;
        if (!(d <= d3)) return false;
        if (!(d3 <= d2)) return false;
        return true;
    }
    public static final float toRadian(float toRadian) {
        return toRadian * (float)Math.PI / 180.0f;
    }

    @Listener
    private void onMotionFactor(MotionUpdateMultiplierEvent event) {
        if (fullNullCheck()) return;
        if(isDisabled()) return;
        if(!canDrag)return;
        event.setFactor(dragTicks.getValue());
        canDrag = false;
    }

    @Listener
    private void onMoving(MoveEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if(isDisabled()) return;
        EntityPlayerSP entityPlayerSP = mc.player;
        if (!EntityUtil.isEntityAlive(entityPlayerSP)) {
            this.disable();
            return;
        }
        EntityPlayerSP entityPlayerSP2 = mc.player;
        double currentSpeed = MovementUtils.getSpeed(entityPlayerSP2);
        if (this.shouldDisable(currentSpeed)) {
            this.disable();
            return;
        }
        BlockPos blockPos = this.getHole();
        if (blockPos != null&&!blockPos.equals(new BlockPos.MutableBlockPos(0, -69, 0))) {

            canDrag = true;

            if (disableStrafe.getValue()) {
                //nospeed
                if( Objects.requireNonNull(ModuleManager.getModuleByName("Speed")).isEnabled()){
                    ModuleManager.getModuleByName("Speed").disable();
                    if(Openafterclosing.getValue()){
                        disableStrafe1 = true;
                    }
                }
                if( Objects.requireNonNull(ModuleManager.getModuleByName("ReverseStep")).isEnabled()){
                    ModuleManager.getModuleByName("ReverseStep").disable();
                    if(OpenafterclosingReverseStep.getValue()){
                        OpenafterclosingReverseStep2 = true;
                    }
                }
            }

            if (airStrafe.getValue() || mc.player.onGround) {
                if (!isCentered(mc.player, blockPos)) {

                    double d;
                    Vec3d playerPos = mc.player.getPositionVector();
                    Vec3d targetPos = new Vec3d((double)blockPos.getX() + 0.5, mc.player.posY, (double)blockPos.getZ() + 0.5);
                    float yawRad = toRadian(getRotationTo(playerPos, targetPos).x);

                    double dist = playerPos.distanceTo(targetPos);

                    if (mc.player.onGround) {
                        double d2 = 0.2805;
                        double d3 = dist / 2.0;
                        d = Math.min(d2, d3);
                    } else {
                        d = currentSpeed + 0.02;
                    }
                    double speed = d;
                    event.setX((double)(-((float)Math.sin(yawRad))) * speed);
                    event.setZ((double)((float)Math.cos(yawRad)) * speed);

                      if (mc.player.collidedHorizontally) {
                          if(Step.getValue()&&mc.player.onGround){
                              if (mc.player != null && mc.player.onGround && !mc.player.isInWater() && !mc.player.isOnLadder() ) {
                                  for (double n = 0.0; n < 2 + 0.5; n += 0.01) {
                                      if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -n, 0.0)).isEmpty()) {
                                          mc.player.motionY = -10.0;
                                          break;
                                      }
                                  }
                              }

                                      ++stuckTicks2;
                              if(stuckTicks2>5){
                                  mc.player.stepHeight = 0.5f;
                                  ++stuckTicks ;
                              }
                          }else {
                              ++stuckTicks ;
                          }

                      } else {
                          stuckTicks = 0;
                      }

                }
            }
        }

    }

    public static final int roundToInt(double roundToInt)  {
        double d = roundToInt;
        boolean bl = false;
        if (Double.isNaN(d)) {
            throw new IllegalArgumentException("Cannot round NaN value.");
        }
        if (roundToInt > (double)Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (roundToInt < (double)Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        int n = (int)Math.round(roundToInt);
        return n;
    }
    private List<BlockPos> getBlockPos(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        int x;
        ArrayList<BlockPos> returnList = new ArrayList<BlockPos>();
        int n = minX;
        if (n > maxX) return returnList;
        do {
            int z;
            x = n++;
            int n2 = minZ;
            if (n2 > maxZ) continue;
            do {
                int y;
                z = n2++;
                int n3 = minY;
                if (n3 > maxY) continue;
                do {
                    y = n3++;
                    returnList.add(new BlockPos(x, y, z));
                } while (y != maxY);
            } while (z != maxZ);
        } while (x != maxX);
        return returnList;
    }
    public final List<BlockPos> getBlockPositionsInArea(BlockPos pos1,BlockPos pos2) {
        int n = pos1.getX();
        int n2 = pos2.getX();
        int n3 = 0;
        int minX = Math.min(n, n2);
        n2 = pos1.getX();
        n3 = pos2.getX();
        int n4 = 0;
        int maxX = Math.max(n2, n3);
        n3 = pos1.getY();
        n4 = pos2.getY();
        int n5 = 0;
        int minY = Math.min(n3, n4);
        n4 = pos1.getY();
        n5 = pos2.getY();
        int n6 = 0;
        int maxY = Math.max(n4, n5);
        n5 = pos1.getZ();
        n6 = pos2.getZ();
        int n7 = 0;
        int minZ = Math.min(n5, n6);
        n6 = pos1.getZ();
        n7 = pos2.getZ();
        boolean bl = false;
        int maxZ = Math.max(n6, n7);
        return this.getBlockPos(minX, maxX, minY, maxY, minZ, maxZ);
    }
    public final double distanceTo(Entity distanceTo, Vec3i vec3i) {
        double xDiff = (double)vec3i.getX() + 0.5 - distanceTo.posX;
        double yDiff = (double)vec3i.getY() + 0.5 - distanceTo.posY;
        double zDiff = (double)vec3i.getZ() + 0.5 - distanceTo.posZ;
        double d = xDiff * xDiff + yDiff * yDiff + zDiff * zDiff;
        return Math.sqrt(d);
    }
    public static final IBlockState getState( BlockPos state) {
        IBlockState iBlockState = mc.world.getBlockState(state);
        return iBlockState;
    }
    public static final Block getBlock(BlockPos blocks) {
        Block block = getState(blocks).getBlock();
        return block;
    }
    private final boolean checkBlock(Block block) {

        if (block==Blocks.BEDROCK) return true;
        if (block== Blocks.OBSIDIAN) return true;
        if (block== Blocks.ENDER_CHEST) return true;
        if (block== Blocks.ANVIL) return true;
        return false;
    }
    public final HoleUtil.HoleType checkHole(BlockPos pos) {
        if (!mc.world.isAirBlock(pos)) return HoleUtil.HoleType.NONE;
        BlockPos blockPos = pos.up();
        if (!mc.world.isAirBlock(blockPos)) return HoleUtil.HoleType.NONE;
        blockPos = pos.up(2);
        if (!mc.world.isAirBlock(blockPos)) {
            return HoleUtil.HoleType.NONE;
        }
        HoleUtil.HoleType type = HoleUtil.HoleType.CUSTOM;
        BlockPos[] blockPosArray = surroundOffset;
        int n = 0;
        int n2 = blockPosArray.length;
        while (n < n2) {
            BlockPos offset = blockPosArray[n];
            ++n;
            BlockPos blockPos2 = pos.add(offset);
            Block block = getBlock(blockPos2);
            if (!this.checkBlock(block)) {
                return HoleUtil.HoleType.NONE;
            }
            if (block==Blocks.BEDROCK) continue;
            type = HoleUtil.HoleType.SINGLE;
        }
        return type;
    }
    private final BlockPos findHole() {
        Pair<Double, BlockPos> closestHole = new Pair<>(69.69, BlockPos.ORIGIN);
        EntityPlayerSP entityPlayerSP = mc.player;
        BlockPos playerPos = EntityUtil.getFlooredPosition(entityPlayerSP);

        int ceilRange = (int) Math.ceil(range.getValue());


        List<BlockPos> posList = getBlockPositionsInArea( playerPos.add(ceilRange, -1, ceilRange),  playerPos.add(-ceilRange, -1, -ceilRange));
        Iterator<BlockPos> inc = posList.iterator();
        block : while (true) {
            double dist;
            BlockPos posXZ;


            if (inc.hasNext()) {
                posXZ = inc.next();
                dist = distanceTo(mc.player, posXZ);
                if (dist >  Double.valueOf(range.getValue()) || dist > (closestHole.getKey())) continue;
            } else {
                if (closestHole.getValue()==BlockPos.ORIGIN) return null;
                BlockPos it = closestHole.getValue();

                holePos.setPos(it);
                return it;
            }

            int n = 0;
            do {
                int posY = n++;
                BlockPos pos = posXZ.add(0, -posY, 0);
                BlockPos blockPos3 = pos.up();
                if (!mc.world.isAirBlock(blockPos3)) continue block;
                if (checkHole(pos) == HoleUtil.HoleType.NONE) continue;
                closestHole = to(dist, pos);
            } while (n <= 5);
        }
    }

    public static final <A, B> Pair<A, B> to(A to, B that) {
        return new Pair<>(to, that);
    }
    private final boolean shouldDisable(double currentSpeed) {
        boolean bl;
        if (holePos!=null) {
            if (mc.player.posY < (double)holePos.getY()) {
                return true;
            }
            bl = false;
        } else {
            bl = false;
        }
        if (bl) return true;
        if (stuckTicks > 5) {
            if (currentSpeed < 0.1) return true;
        }
        if (!(currentSpeed < 0.01)) return false;
        EntityPlayerSP entityPlayerSP = mc.player;




        if (checkHole(EntityUtil.getFlooredPosition(entityPlayerSP)) == HoleUtil.HoleType.NONE) return false;
        return true;
    }
    private final BlockPos getHole() {
        BlockPos blockPos;
        if (mc.player.ticksExisted % 10 == 0) {
            EntityPlayerSP entityPlayerSP =mc.player;
            if (!EntityUtil.getFlooredPosition(entityPlayerSP).equals(holePos)) {
                blockPos = this.findHole();
                return blockPos;
            }
        }

        if (holePos!= null) {
            blockPos = holePos;
            return blockPos;
        }
        blockPos = this.findHole();
        return blockPos;
    }



}
