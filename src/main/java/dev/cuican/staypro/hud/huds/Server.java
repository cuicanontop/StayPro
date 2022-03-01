package dev.cuican.staypro.hud.huds;




import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.engine.AsyncRenderer;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.module.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.Objects;

/**
 * Created by B_312 on 01/03/21
 */
@ModuleInfo(name = "Server",category = Category.HUD)
public class Server extends HUDModule {


    @Override
    public void onHUDRender(ScaledResolution resolution) {

            String Final = "IP " + "\u00a7f" + (mc.isSingleplayer() ? "Single Player" : Objects.requireNonNull(mc.getCurrentServerData()).serverIP.toLowerCase());
            FontManager.fontRenderer.drawString(Final, x + 2, y + 4, GUIManager.getColor3I());
            width = FontManager.getWidth(Final)+ 4;
            height = FontManager.getHeight() + 2;


    }

}
