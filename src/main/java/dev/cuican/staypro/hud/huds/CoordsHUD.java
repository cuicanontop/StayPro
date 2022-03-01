package dev.cuican.staypro.hud.huds;


import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.utils.ChatUtil;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

/**
 * Created by B_312 on 01/03/21
 */
@ModuleInfo(name = "CoordsHUD", category = Category.HUD)
public class CoordsHUD extends HUDModule {


    @Override
    public void onHUDRender(ScaledResolution resolution) {

        if(mc.player == null || mc.world == null){
            return;
        }
        if(isDisabled())return;
        boolean inHell = mc.player.dimension == -1;
        float f = !inHell ? 0.125f : 8.0f;

        String posX = String.format("%.1f", mc.player.posX);
        String posY = String.format("%.1f", mc.player.posY);
        String posZ = String.format("%.1f", mc.player.posZ);
        String hposX = String.format("%.1f", mc.player.posX * (double) f);
        String hposZ = String.format("%.1f", mc.player.posZ * (double) f);
        String ow = posX + ", " + posY + ", " + posZ;
        String nether = hposX + ", " + posY + ", " + hposZ;

        String Final = ChatUtil.SECTIONSIGN + "rXYZ " + ChatUtil.SECTIONSIGN + "f" + ow + ChatUtil.SECTIONSIGN + "r [" + ChatUtil.SECTIONSIGN + "f" + nether + ChatUtil.SECTIONSIGN + "r]";

        FontManager.fontRenderer.drawString(Final, this.x + 2, this.y + 4 , GUIManager.getColor3I());
        width = FontManager.getWidth(Final)+ 4;
        height = FontManager.getHeight() + 2;


    }

}
