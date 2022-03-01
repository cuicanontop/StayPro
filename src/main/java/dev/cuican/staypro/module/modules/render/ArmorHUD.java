package dev.cuican.staypro.module.modules.render;


import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.event.events.render.RenderOverlayEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;

@ModuleInfo(name = "ArmorHUD", category = Category.RENDER,description = "ArmorHUD")
public class ArmorHUD extends Module {
    public static RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
    Setting<Boolean> damage = setting("Damage",true);

    @Override
    public void onRender(RenderOverlayEvent event) {

        GlStateManager.enableTexture2D();
        ScaledResolution resolution = new ScaledResolution(mc);
        int i = resolution.getScaledWidth() / 2;
        int iteration = 0;
        int y = resolution.getScaledHeight() - 55 - (mc.player.isInWater() ? 10 : 0);
        for (ItemStack is : mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) {
                continue;
            }
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            itemRender.zLevel = 200.0f;
            itemRender.renderItemAndEffectIntoGUI(is, x, y);
            itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
            itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            mc.fontRenderer.drawStringWithShadow(s, (float)(x + 19 - 2 - mc.fontRenderer.getStringWidth(s)), (float)(y + 9), 16777215);
            if (!this.damage.getValue()) {
                continue;
            }
            this.drawDamage(is, x, y);
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }



    public void drawDamage(ItemStack itemstack, int x, int y) {
        float green = ((float)itemstack.getMaxDamage() - (float)itemstack.getItemDamage()) / (float)itemstack.getMaxDamage();
        float red = 1.0f - green;
        int dmg = 100 - (int)(red * 100.0f);
        mc.fontRenderer.drawStringWithShadow(dmg + "", (float)(x + 8 - mc.fontRenderer.getStringWidth(dmg + "") / 2), (float)(y - 11),GUIManager.getColor3I() );
    }



}