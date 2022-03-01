package dev.cuican.staypro.hud.huds;



import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.engine.AsyncRenderer;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.math.LagCompensator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

/**
 * Created by B_312 on 01/03/21
 */
@ModuleInfo(name = "TPS", category = Category.HUD)
public class TPS extends HUDModule {


    public TPS() {
        asyncRenderer = new AsyncRenderer() {
            @Override
            public void onUpdate(ScaledResolution resolution, int mouseX, int mouseY) {
                String Final = "TPS " + ChatUtil.SECTIONSIGN + "f" + String.format("%.2f",LagCompensator.INSTANCE.getTickRate());

                drawAsyncString(Final, x + 2, y + 4, GUIManager.getColor3I());

                width = mc.fontRenderer.getStringWidth(Final) + 4;
                height = FontManager.getHeight();
            }
        };
    }

    @Override
    public void onHUDRender(ScaledResolution resolution) {
        asyncRenderer.onRender();
    }

}
