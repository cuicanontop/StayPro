package dev.cuican.staypro.module.modules.combat;

import dev.cuican.staypro.client.FriendManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(name = "AutoBurrow", category = Category.COMBAT, description = "AutoBurrow")
public class AutoBurrow extends Module {
    private final Setting<Boolean> breakCrystal = setting("Crystal", true);
    private final Setting<Double> hpmax = setting("Crystal Max HP:", 13.0, 0.0, 36.0).whenTrue(breakCrystal);
    private final Setting<Double> hpmin = setting("Crystal Min HP:", 3.0, 0.0, 36.0).whenTrue(breakCrystal);
    private final Setting<Double> hpDistance = setting("Crystal Distance:", 5.5, 1.0, 36.0).whenTrue(breakCrystal);
    private final Setting<Boolean> Hole = setting("Hole", true);
    private final Setting<Double> Holedistance = setting("Hole Distance:", 3.0, 0.0, 6.0).whenTrue(Hole);
    private final Setting<Boolean> ontuo = setting("Only one", true);
    private final Setting<Boolean> rotate = setting("Rotate", false);
    public Setting<Boolean> center = setting("TPCenter", false);

    private boolean inBurrow = false;
    private BlockPos inpos = null;

    @Override
    public void onTick() {
        if (fullNullCheck()) return;
        if (BurrowUtil.isInsideBlock()) inBurrow = false;
        if (!mc.world.getBlockState(mc.player.getPosition().up(1)).getBlock().equals(Blocks.AIR)) return;
        if (breakCrystal.getValue()) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityEnderCrystal)) continue;
                if (BurrowUtil.isInsideBlock()) return;
                if (mc.player.getDistance(entity) > 12f) continue;
                double maxDmg = CrystalUtil.calculateDamage(entity.posX, entity.posY, entity.posZ, mc.player) - 0.5;
                EntityPlayer cdis = GetUtil.find_closest_target(20);
                if (cdis != null
                        && hpmin.getValue() <= maxDmg
                        && getDistance(cdis, new BlockPos(entity)) <= hpDistance.getValue()
                ) {

                    if (mc.player.getDistance(entity) <= 2.0
                            && maxDmg <= hpmax.getValue()
                            && maxDmg < mc.player.getHealth() + mc.player.getAbsorptionAmount() - 0.5
                    ) {
                        mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
                        inBurrow = true;
                    }
                    if(mc.player.getDistance(entity)>2.0){
                        inBurrow = true;
                    }




                }else {
                    inBurrow = false;
                }
            }
        }

        if (Hole.getValue()) {

            EntityPlayer od =GetUtil.find_closest_target(Holedistance.getValue());
            if(!inBurrow)
                inBurrow = od != null
                    && HoleUtil.isInHole()
                    && !BurrowUtil.isInsideBlock()
                    && od.posY - 0.8 >= mc.player.posY;
        }

        if (fullNullCheck()) return;
        if (inBurrow
                && !BurrowUtil.isInsideBlock()
                && mc.player.onGround
        ) {

            BlockPos post = new BlockPos(mc.player);

            if(ontuo.getValue()&&inpos!=null&&inpos.equals(post)){
                inBurrow = false;
                return;
            }
          if(  BurrowUtil.burrow(rotate.getValue(), center.getValue()))inpos = post;

        }
    }
    transient Timer placeTimer = new Timer();

    public double getDistance(EntityPlayer target, BlockPos pos) {
        return Math.sqrt((target.posX - pos.getX()) * (target.posX - pos.getX()) + (target.posY - pos.getY()) * (target.posY - pos.getY()) + (target.posZ - pos.getZ()) * (target.posZ - pos.getZ()));
    }


}
