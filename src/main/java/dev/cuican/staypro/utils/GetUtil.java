package dev.cuican.staypro.utils;

import dev.cuican.staypro.client.FriendManager;
import net.minecraft.entity.player.EntityPlayer;

public class GetUtil extends Wrapper{
    public static EntityPlayer find_closest_target(double range) {
        if (mc.world.playerEntities.isEmpty()) {
            return null;
        }
        EntityPlayer closestTarget = null;
        for (EntityPlayer target : mc.world.playerEntities) {
            if (target == mc.player)
                continue;
            if (mc.player.getDistance(target) > range)
                continue;
            if (FriendManager.isFriend(target.getName()))
                continue;
            if (target.getHealth() <= 0.0f)
                continue;
            if (closestTarget != null)
                if (mc.player.getDistance(target) > mc.player.getDistance(closestTarget))
                    continue;
            closestTarget = target;
        }
        return closestTarget;
    }
}
