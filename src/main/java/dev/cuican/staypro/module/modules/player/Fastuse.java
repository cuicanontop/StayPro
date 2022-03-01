package dev.cuican.staypro.module.modules.player;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.mixin.accessor.AccessorMinecraft;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;

/**
 * Created by S-B99 on 23/10/2019
 *
 * @author S-B99
 * Updated by S-B99 on 03/12/19
 * Updated by d1gress/Qther on 4/12/19
 */
@ModuleInfo(category = Category.PLAYER, description = "Use items faster", name = "FastUse")
public class Fastuse extends Module {

    private static long time = 0;
    private final Setting<Integer> delay = setting("Delay", 0, 0, 10);
    private final Setting<Boolean> all = setting("All", false);
    private final Setting<Boolean> bow = setting("Bow", false).whenFalse(all);
    private final Setting<Boolean> expBottles = setting("XP", true).whenFalse( all);
    private final Setting<Boolean> endCrystals = setting("Crystal", false).whenFalse( all);
    private final Setting<Boolean> fireworks = setting("FireWorks", false).whenFalse( all);

    @Override
    public void onDisable() {
        ((AccessorMinecraft) mc).setRightClickDelayTimer(2);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;

        if (all.getValue() || bow.getValue() && mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
            mc.player.stopActiveHand();
        }
        if (all.getValue() || bow.getValue() && mc.player.getHeldItemOffhand().getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
            mc.player.stopActiveHand();
        }
        if (all.getValue() || expBottles.getValue() && mc.player.getHeldItemOffhand().getItem() instanceof ItemExpBottle && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
            mc.player.stopActiveHand();
        }

        if (!(delay.getValue() <= 0)) {
            if (time <= 0) time = Math.round((2 * (Math.round((float) delay.getValue() / 2))));
            else {
                time--;
                ((AccessorMinecraft) mc).setRightClickDelayTimer(1);
                return;
            }
        }

        if (passItemCheck(mc.player.getHeldItemMainhand().getItem()) || passItemCheck(mc.player.getHeldItemOffhand().getItem())) {
            ((AccessorMinecraft) mc).setRightClickDelayTimer(0);
        }
        if (passItemCheck(mc.player.getHeldItemOffhand().getItem())) {
            ((AccessorMinecraft) mc).setRightClickDelayTimer(0);
        }
    }

    private boolean passItemCheck(Item item) {
        if (all.getValue()) return true;
        if (expBottles.getValue() && item instanceof ItemExpBottle) return true;
        if (endCrystals.getValue() && item instanceof ItemEndCrystal) return true;
        return fireworks.getValue() && item instanceof ItemFirework;
    }
}
