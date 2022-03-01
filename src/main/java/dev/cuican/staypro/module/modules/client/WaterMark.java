package dev.cuican.staypro.module.modules.client;

import dev.cuican.staypro.Stay;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.event.events.render.RenderOverlayEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.utils.ChatUtil;

import java.awt.*;

@Parallel
@ModuleInfo(name = "WaterMark", category = Category.CLIENT, description = "Display the Stay watermark")
public class WaterMark extends Module {

    private final Setting<Integer> x = setting("X", 0, 0, 3840);
    private final Setting<Integer> y = setting("Y", 0, 0, 2160);

    @Override
    public void onRender(RenderOverlayEvent event) {
        int color = GUIManager.isRainbow() ? rainbow(1) : GUIManager.getColor3I();
        FontManager.draw(Stay.MOD_NAME + " " + ChatUtil.SECTIONSIGN + "f" + Stay.MOD_VERSION, x.getValue() + 1, y.getValue() + 3, color);
    }

    public int rainbow(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 1.0f, 1.0f).getRGB();
    }

}
