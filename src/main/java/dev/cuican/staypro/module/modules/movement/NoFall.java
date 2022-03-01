package dev.cuican.staypro.module.modules.movement;

import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.event.events.network.PacketEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.EntityUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

@ModuleInfo(name = "NoFall", category = Category.MOVEMENT, description = "Prevents fall damage.")
public class NoFall extends Module {
    Setting<String> fallMode = setting("Mode", "BUCKET", listOf("BUCKET", "PACKET"));
    public  final Setting<Boolean> pickup = setting("PickUp", true).whenAtMode(fallMode,"BUCKET");
    public Setting<Integer> distance = setting("Distance",3,1,10).whenAtMode(fallMode, "BUCKET");
    public Setting<Integer> pickupDelay = setting("Delay",300,100,1000).whenAtMode(fallMode, "BUCKET");
    private long last = 0;

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if ((fallMode.getValue().equals("PACKET")) && event.getPacket() instanceof CPacketPlayer) {
            ((CPacketPlayer) event.getPacket()).onGround = true;
        }
    }

    @Override
    public void onTick() {
        if ((fallMode.getValue().equals("BUCKET")) && mc.player.fallDistance >= distance.getValue() && !EntityUtil.isAboveWater(mc.player) && System.currentTimeMillis() - last > 100) {
            Vec3d posVec = mc.player.getPositionVector();
            RayTraceResult result = mc.world.rayTraceBlocks(posVec, posVec.add(0, -5.33f, 0), true, true, false);
            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                EnumHand hand = EnumHand.MAIN_HAND;
                if (mc.player.getHeldItemOffhand().getItem() == Items.WATER_BUCKET) hand = EnumHand.OFF_HAND;
                else if (mc.player.getHeldItemMainhand().getItem() != Items.WATER_BUCKET) {
                    for (int i = 0; i < 9; i++)
                        if (mc.player.inventory.getStackInSlot(i).getItem() == Items.WATER_BUCKET) {
                            mc.player.inventory.currentItem = i;
                            mc.player.rotationPitch = 90;
                            last = System.currentTimeMillis();
                            return;
                        }
                    return;
                }
                mc.player.rotationPitch = 90;
                mc.playerController.processRightClick(mc.player, mc.world, hand);
            }
            if (pickup.getValue()) {
                new Thread(() -> {
                    try {
                        Thread.sleep(pickupDelay.getValue());
                    } catch (InterruptedException ignored) {
                    }
                    mc.player.rotationPitch = 90;
                    mc.rightClickMouse();
                }).start();
            }
        }
    }


}
