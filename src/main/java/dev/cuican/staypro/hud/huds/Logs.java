package dev.cuican.staypro.hud.huds;

import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.ColorUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

@ModuleInfo(name = "Logs", category = Category.HUD,description = "Puts a logo there (there)")
public class Logs extends HUDModule {
    public static final ResourceLocation mark = new ResourceLocation("textures/logo.png");
    public Setting<Integer> imageWidth = setting("logoWidth", 25, 0, 1000);
    public Setting<Integer> imageHeight =setting("logoHeight", 25, 0, 1000);
    private final Setting<Integer> Scale = setting("Size", 5, 0, 100);
    @Override
    public void onHUDRender(ScaledResolution resolution) {
        if (!fullNullCheck()) {
            if (isEnabled()) {
                this.renderLogo();
            }
    }

}

    public void renderLogo() {
        if(isDisabled())return;
        int widths = this.imageWidth.getValue();
        int heights = this.imageHeight.getValue();
        mc.renderEngine.bindTexture(mark);
        GlStateManager.color(255.0F, 255.0F, 255.0F);
       Gui.drawScaledCustomSizeModalRect(x , y , Scale.getValue(), Scale.getValue(), widths - Scale.getValue(), heights - Scale.getValue(), width, height, (float)widths, (float)heights);
         width = widths+ 8;
        height = heights + 4;
    }
}
