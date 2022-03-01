package dev.cuican.staypro.module.modules.render;


import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

/**
 * @author linustouchtips
 * @since 11/27/2020
 */
@ModuleInfo(name = "FullBright", description = "HandColor", category = Category.RENDER)
public class FullBright extends Module {
    Setting<String> mode = setting("Swing", "Gamma", listOf("Gamma", "Potion"));



    float oldBright;

    @Override
    public void onTick() {
        if (nullCheck()) {
            return;
        }

        if (mode.getValue().equals("Potion")) {
            mc.player.addPotionEffect(new PotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 80950, 1, false, false)));
        }
    }

    @Override
    public void onEnable() {
        if (nullCheck()) {
            return;
        }

        oldBright = mc.gameSettings.gammaSetting;

        if (mode.getValue().equals("Gamma")) {
            mc.gameSettings.gammaSetting = +100;
        }

    }

    @Override
    public void onDisable() {
        mc.player.removePotionEffect(MobEffects.NIGHT_VISION);

        if (mode.getValue().equals("Gamma")) {
            mc.gameSettings.gammaSetting = oldBright;
        }
    }
}
