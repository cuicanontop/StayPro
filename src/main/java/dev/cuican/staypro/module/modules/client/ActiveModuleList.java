package dev.cuican.staypro.module.modules.client;

import dev.cuican.staypro.Stay;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.engine.AsyncRenderer;
import dev.cuican.staypro.engine.RenderEngine;
import dev.cuican.staypro.event.events.render.RenderOverlayEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.utils.graphics.RenderHelper;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Parallel
@ModuleInfo(name = "ActiveModuleList", category = Category.CLIENT, description = "Show the active modules")
public class ActiveModuleList extends Module {

    Setting<String> listPos = setting("ListPos", "RightTop", listOf("RightTop", "RightDown", "LeftTop", "LeftDown"), "The position of list");

    AsyncRenderer asyncRenderer = new AsyncRenderer() {
        @Override
        public void onUpdate(ScaledResolution resolution, int mouseX, int mouseY) {
            int startX = RenderHelper.getStart(resolution, listPos.getValue()).x;
            int startY = RenderHelper.getStart(resolution, listPos.getValue()).y;

            if (mc.player.getActivePotionEffects().size() > 0 && listPos.getValue().equals("RightTop")) {
                startY += 26;
            }

            int index = 0;

            List<Module> moduleList = Stay.MODULE_BUS.getModules().stream()
                    .sorted(Comparator.comparing(it -> -FontManager.getWidth(it.getHudSuffix()))).collect(Collectors.toList());

            for (Module module : moduleList) {
                int color = GUIManager.isRainbow() ? rainbow(index * 100) : GUIManager.getColor3I();
                index++;
                String information = module.getHudSuffix();
                switch (listPos.getValue()) {
                    case "RightDown": {
                        drawAsyncString(information, startX - FontManager.getWidth(information), startY - FontManager.getHeight() * index, color);
                        break;
                    }
                    case "LeftTop": {
                        drawAsyncString(information, startX, startY + 3 + FontManager.getHeight() * (index - 1), color);
                        break;
                    }
                    case "LeftDown": {
                        drawAsyncString(information, startX, startY - FontManager.getHeight() * index, color);
                        break;
                    }
                    default: {
                        drawAsyncString(information, startX - FontManager.getWidth(information), startY + 3 + FontManager.getHeight() * (index - 1), color);
                        break;
                    }
                }
            }
        }
    };

    @Override
    public void onEnable() {
        RenderEngine.subscribe(asyncRenderer);
    }

    @Override
    public void onDisable() {
        RenderEngine.unsubscribe(asyncRenderer);
    }

    @Override
    public void onRender(RenderOverlayEvent event) {
        asyncRenderer.onRender();
    }

    public int rainbow(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 1.0f, 1.0f).getRGB();
    }

}
