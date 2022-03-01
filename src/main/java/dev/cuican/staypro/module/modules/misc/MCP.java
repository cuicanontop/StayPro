package dev.cuican.staypro.module.modules.misc;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.utils.inventory.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

@ModuleInfo(name = "MCP", category = Category.MISC)
public class MCP extends Module {
    public int oldSlot = -1;

    @Override
    public void onEnable() {
        if (fullNullCheck()) {
            return;
        }
        oldSlot = mc.player.inventory.currentItem;
    }

    @Override
    public void onTick() {
        if (fullNullCheck() || mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (Mouse.isButtonDown(2)) {
            oldSlot = mc.player.inventory.currentItem;
            RayTraceResult var2 = mc.objectMouseOver;
            if (var2.typeOfHit != RayTraceResult.Type.ENTITY && var2.typeOfHit != RayTraceResult.Type.BLOCK) {
                int p = InventoryUtil.findHotbarItem(ItemEnderPearl.class);
                if (p == -1) {
                    return;
                }
                InventoryUtil.switchToHotbarSlot(p, false);
                try {
                    mc.playerController.processRightClick(mc.player, mc.world, mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                }catch (Exception ignored){}
                InventoryUtil.switchToHotbarSlot(oldSlot, false);
            }
        }
    }
}
