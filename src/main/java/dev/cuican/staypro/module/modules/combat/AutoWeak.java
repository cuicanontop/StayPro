package dev.cuican.staypro.module.modules.combat;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.concurrent.event.Priority;
import dev.cuican.staypro.event.events.client.EventMotion;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.event.events.render.RenderModelEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.GetUtil;
import dev.cuican.staypro.utils.HoleUtil;
import dev.cuican.staypro.utils.RotationUtil;
import dev.cuican.staypro.utils.Timer;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.awt.event.InputEvent;

@ModuleInfo(name = "AutoWeak", category = Category.COMBAT,description = "AutoWeak")
public class AutoWeak extends Module {
    private Timer isTimer = new Timer();
    private final Setting<Boolean> ontuo = setting("Only one", true);
    private int slot = -1;
    private boolean inrot = false;

    private int isArrowweak() {
        int inInv = -1;

        for(int i = 0; i < 36; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.TIPPED_ARROW&& itemStack.stackTagCompound.getString("Potion").equals("minecraft:long_weakness")) {
                inInv = i;

                break;
            }
        }

        return inInv;
    }



    private BlockPos pos = null;
    private  Robot robot;
    @Override
    public void onTick() {
        if(fullNullCheck())return;
        if(!HoleUtil.isInHole()){
            pos = null;
            return;
        }

        if (!mc.world.getBlockState(mc.player.getPosition().up(1)).getBlock().equals(Blocks.AIR)) return;
        try { robot = new Robot(); } catch (AWTException ignored) {return; }
        if (mc.currentScreen == null && mc.player.onGround){
            EntityPlayer enemy = GetUtil.find_closest_target(3);
            if(enemy == null||
                    !new BlockPos(mc.player).equals(new BlockPos(enemy))
                    ||enemy.isPotionActive(MobEffects.WEAKNESS)||isArrowweak()==-1
            ){
               pos = null;
                return;
            }
            BlockPos post = new BlockPos(mc.player);
            if(ontuo.getValue()&&pos!=null&&post.equals(pos))return;
            if(slot==-1)slot = mc.player.inventory.currentItem;
            int bowslot = InventoryUtil.findItem(Items.BOW);
            if(bowslot!=-1) mc.player.inventory.currentItem =  bowslot;
            int weak = isArrowweak();
            if (mc.player.getHeldItemMainhand().getItem() == Items.BOW) {
                if(mc.player.getItemInUseMaxCount() <= 3){
                    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, weak, 9, ClickType.SWAP, mc.player);
                    mc.playerController.updateController();
                    lookAt( mc.player.getEntityBoundingBox().offset(0, 1, 0).getCenter());
                    mc.player.rotationPitch += 0.0004;
                    robot.mousePress(InputEvent.BUTTON3_MASK);
                    mc.playerController.updateController();
                }else {
                    robot.mouseRelease(InputEvent.BUTTON3_MASK);
                    lookAt( mc.player.getEntityBoundingBox().offset(0, 1, 0).getCenter());
                    mc.player.rotationPitch += 0.0004;
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                    mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                    mc.player.stopActiveHand();
                    pos = new BlockPos(mc.player);
                    mc.playerController.updateController();
                    if(slot!=-1)mc.player.inventory.currentItem = slot;
                    slot=-1;
                    mc.playerController.updateController();
                }

            }

        }
    }

    public void lookAt(Vec3d vec3d) {
        float[] v = RotationUtil.getRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), vec3d);
        float[] v2 = RotationUtil.getRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), vec3d.add(0, -0.5, 0));
        setYawAndPitch(v[0], v[1], v2[1]);
    }
    transient public static float yaw;
    transient public static float pitch;
    transient public static float renderPitch;



    public void setYawAndPitch(float yaw1, float pitch1, float renderPitch1) {
        yaw = yaw1;
        pitch = pitch1;
        renderPitch = renderPitch1;
        mc.player.rotationYawHead = yaw;
        mc.player.renderYawOffset = yaw;


    }

}
