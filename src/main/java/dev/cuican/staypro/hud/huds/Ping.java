package dev.cuican.staypro.hud.huds;


import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.engine.AsyncRenderer;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.math.LagCompensator;
import net.minecraft.client.gui.ScaledResolution;

@ModuleInfo(name = "Ping", category = Category.HUD)
public class Ping extends HUDModule {


    public Ping() {
        asyncRenderer = new AsyncRenderer() {
            @Override
            public void onUpdate(ScaledResolution resolution, int mouseX, int mouseY) {

                int privatePingValue = LagCompensator.globalInfoPingValue();
                String Final = "Ping " + ChatUtil.SECTIONSIGN + "f" + privatePingValue;
                drawAsyncString(Final, x+ 2, y+ 4, GUIManager.getColor3I());
                width =mc.fontRenderer.getStringWidth(Final) + 4;
                height = FontManager.getHeight();
            }
        };
    }

    @Override
    public void onHUDRender(ScaledResolution resolution) {
        asyncRenderer.onRender();
    }


}