package dev.cuican.staypro.hud.huds;

import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.module.Category;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@ModuleInfo(name = "CrystalHUD", category = Category.HUD)
public class CrystalHUD extends HUDModule {




    @Override
    public void onHUDRender(ScaledResolution resolution) {
        renderCrystalHUD();
    }




    private static final ItemStack Crystal = new ItemStack(Items.END_CRYSTAL);



    public void renderCrystalHUD() {
        if(isDisabled())return;
        int Crystals = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.END_CRYSTAL)).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            Crystals += mc.player.getHeldItemOffhand().getCount();
        }
        if (Crystals > 0) {
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            mc.getRenderItem().zLevel = 200.0F;
            mc.getRenderItem().renderItemAndEffectIntoGUI(Crystal, x, y);
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, Crystal, x, y, "");
            mc.getRenderItem().zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            FontManager.fontRenderer.drawStringWithShadow(Crystals + "", (x + 19 - 2 - mc.fontRenderer.getStringWidth(Crystals + "")), (y + 9), 16777215);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
            width =  16;
            height =  16;
        }
    }


}
