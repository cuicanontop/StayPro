package dev.cuican.staypro.hud.huds;


import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.engine.AsyncRenderer;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import org.lwjgl.input.Mouse;

@ModuleInfo(name = "Players", category = Category.HUD)
public class Player extends HUDModule {


    @Override
    public void onHUDRender(ScaledResolution resolution) {
        asyncRenderer.onRender();
    }
    public Player() {
        asyncRenderer = new AsyncRenderer() {
            @Override
            public void onUpdate(ScaledResolution resolution, int mouseX, int mouseY) {
                if(mc.player == null || mc.world == null){
                    return;
                }
                int onlinePlayer = mc.player.connection.getPlayerInfoMap().size();
               String is ="";
               if (onlinePlayer > 1){
                   is =  "s";
               }else {
                   is = "";
               }

              String finals = "Player" + is + " " + ChatUtil.SECTIONSIGN + "f" + onlinePlayer;
                drawAsyncString(finals, x + 2, y + 4, GUIManager.getColor3I());
                width = mc.fontRenderer.getStringWidth(finals) * 2;
                height = FontManager.getHeight();
            }
        };
    }

}
