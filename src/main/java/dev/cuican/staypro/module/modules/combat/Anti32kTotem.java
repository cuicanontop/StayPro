package dev.cuican.staypro.module.modules.combat;

import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;

/**
 * Created by B_312 on 06/24/19
 */
@Parallel(runnable = true)
@ModuleInfo(name = "Anti32KTotem", category = Category.COMBAT, description = "Auto refill totem on main hand slot 1")
public class Anti32kTotem extends Module {

    Setting<Boolean> pauseInInventory = setting("PauseInInventory", false);

    @Override
    public void onRenderTick() {

        if (mc.currentScreen != null) {
            return;
        }
        if (pauseInInventory.getValue() && mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (mc.player.inventory.getStackInSlot(0).getItem() == Items.TOTEM_OF_UNDYING) {
            return;
        }
        for (int i = 9; i < 35; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.SWAP, mc.player);
                break;
            }
        }
    }

}
