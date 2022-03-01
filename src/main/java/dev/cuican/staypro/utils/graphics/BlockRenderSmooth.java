package dev.cuican.staypro.utils.graphics;


import dev.cuican.staypro.concurrent.utils.Timer;
import dev.cuican.staypro.utils.FadeUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockRenderSmooth {

    private BlockPos lastPos;
    private BlockPos newPos;
    private final FadeUtils fade;
    public static Timer timer = new Timer();

    public BlockRenderSmooth(BlockPos pos, long smoothLength) {
        lastPos = pos;
        newPos = pos;
        fade = new FadeUtils(smoothLength);
    }

    public void setNewPos(BlockPos pos) {
        if (!isPosMatch(pos, newPos) && timer.passed(50)) {
            lastPos = newPos;
            newPos = pos;
            fade.reset();
            timer.reset();
        }
    }

    public Vec3d getRenderPos() {
        return Lerp(PosToVec(lastPos), PosToVec(newPos), (float) fade.easeOutQuad());
    }

    public static Vec3d Lerp(Vec3d from, Vec3d to, float t) {
        if (t <= 0) {
            return from;
        } else if (t >= 1) {
            return to;
        }
        return new Vec3d(t * to.x + (1 - t) * from.x, t * to.y + (1 - t) * from.y, t * to.z + (1 - t) * from.z);
    }

    private static boolean isPosMatch(BlockPos pos, BlockPos pos1) {
        if(pos != null && pos1 != null) {
            return (pos.getX() == pos1.getX()) && (pos.getY() == pos1.getY()) && (pos.getZ() == pos1.getZ());
        }
        return false;
    }

    private static Vec3d PosToVec(BlockPos pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }
}
