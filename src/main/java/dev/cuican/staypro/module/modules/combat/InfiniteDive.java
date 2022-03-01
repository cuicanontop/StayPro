package dev.cuican.staypro.module.modules.combat;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(name = "InfiniteDive", description = "InfiniteDive", category = Category.COMBAT)
public class InfiniteDive extends Module {

    private Setting<Integer> delay = setting("frequency", 10, 1, 20);


    @Override
    public void onTick() {
      if(isInsideBlock()){
          for (int i = 0; i <delay.getValue() ; i++) {
              mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX,mc.player.posY-1,mc.player.posZ,mc.player.onGround));
          }
      }
    }

    public boolean isInsideBlock() {
        double x = mc.player.posX;
        double y = mc.player.posY + 0.20;
        double z = mc.player.posZ;
        return mc.world.getBlockState(new BlockPos(x, y, z)).getMaterial().blocksMovement() || !mc.player.collidedVertically;
    }


}
