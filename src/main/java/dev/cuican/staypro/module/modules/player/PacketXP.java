package dev.cuican.staypro.module.modules.player;



import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.concurrent.event.Priority;
import dev.cuican.staypro.event.events.client.EventMotion;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.event.events.render.RenderModelEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.EntityUtil;
import dev.cuican.staypro.utils.KeyBind;
import dev.cuican.staypro.utils.MathUtil;
import dev.cuican.staypro.utils.look;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author Madmegsox1
 * @since 30/04/2021
 */
@ModuleInfo(name = "PacketXP", category = Category.PLAYER, description = "PacketXP\", \"Allows you to XP instantly")
public class PacketXP extends Module {

    public  final Setting<Boolean> allowTakeOff = setting("AutoMend", false);
    private final Setting<Integer> takeOffVal = setting("Durable%", 100, 0, 100);
    private final Setting<Integer> delay = setting("Delay", 0, 0, 5);
    private final Setting<KeyBind> bind = setting("PacketBind", new KeyBind(Keyboard.KEY_NONE,this::findExpInHotbar));
    public  final Setting<Boolean> rotate = setting("rotate", true);

    private int delay_count;
    int prvSlot;
    public static   Boolean inft=false;
    private float yaw = 0.0F;
    private float pitch = 0.0F;
    private boolean rotating = false;
    private boolean isSneaking;

    @Override
    public void onEnable() {
        delay_count = 0;
        return;
    }


    private void usedXp() {
        if (findExpInHotbar() == -1) {
            return;
        }



        prvSlot = mc.player.inventory.currentItem; //TODO add better rotations
        mc.player.connection.sendPacket(new CPacketHeldItemChange(findExpInHotbar()));
//        Stay.rotationManager.setPlayerRotations(-90,90);
        if(rotate.getValue()){
            this.yaw = 90;
            this.pitch = 90;
            this.rotating = true;

             }

        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        mc.player.connection.sendPacket(new CPacketHeldItemChange(prvSlot));
           if (allowTakeOff.getValue()) {
            takeArmorOff(); //TODO travis add the ArmourMend take off thing
        }

    }

    @Override
    public void onDisable() {
        this.rotating = false;


    }
    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if ( this.rotate.getValue() && this.rotating && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            packet.yaw = this.yaw;
            packet.pitch = this.pitch;
            this.rotating = false;
        }

    }
    @Override
    public void onTick(){
        if (Keyboard.isKeyDown(bind.getValue().getKeyCode()) && mc.currentScreen == null) {
            if(findExpInHotbar()==-1){
                return;
            }
            usedXp();
        }

    }

    private int findExpInHotbar() {
        int slot = 0;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                slot = i;
                break;
            }
        }
        return slot;
    }



    private ItemStack getArmor(int first) {
        return mc.player.inventoryContainer.getInventory().get(first);
    }

    private void takeArmorOff() {
        int slot = 5;
        while (slot <= 8) {
            ItemStack item;
            item = getArmor(slot);
            double max_dam = item.getMaxDamage();
            double dam_left = item.getMaxDamage() - item.getItemDamage();
            double percent = (dam_left / max_dam) * 100;

            if (percent >= takeOffVal.getValue() && !item.equals(Items.AIR)) {
                if (!notInInv(Items.AIR)) {
                    return;
                }
                if (delay_count < delay.getValue()) {
                    delay_count++;
                    return;
                }
                delay_count = 0;

                mc.playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE, mc.player);

            }
            slot++;
        }
    }

    public Boolean notInInv(Item itemOfChoice) {
        int n;
        n = 0;
        if (itemOfChoice == mc.player.getHeldItemOffhand().getItem()) return true;

        for (int i = 35; i >= 0; i--) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == itemOfChoice) {
                return true;

            } else if (item != itemOfChoice) {
                n++;
            }
        }
        if (n >= 35) {

            return false;
        }
        return true;
    }


}
