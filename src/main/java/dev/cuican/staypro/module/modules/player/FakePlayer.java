package dev.cuican.staypro.module.modules.player;

import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.common.annotations.Parallel;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.potion.PotionEffect;

import java.util.UUID;

@Parallel
@ModuleInfo(name = "FakePlayer", category = Category.PLAYER, description = "Spawn a fake player entity in client side")
public class FakePlayer extends Module {

    public static String customName = "None";
    Setting<Integer> health = setting("Health", 10, 0, 36);
    Setting<Boolean> customMode = setting("CustomName", false);
    Setting<String> mode = setting("Name", "CuiCan",
            listOf("CuiCan",
                    "B_312",
                    "popbob",
                    "jared2013",
                    "bachi",
                    "dot5",
                    "FitMC",
                    "Cyri")).whenFalse(customMode);

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) return;
        EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("60569353-f22b-42da-b84b-d706a65c5ddf"), customMode.getValue() ? customName : mode.getValue()));
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        for (PotionEffect potionEffect : mc.player.getActivePotionEffects()) {
            fakePlayer.addPotionEffect(potionEffect);
        }
        fakePlayer.setHealth(health.getValue());
        fakePlayer.inventory.copyInventory(mc.player.inventory);
        fakePlayer.rotationYawHead = mc.player.rotationYawHead;
        mc.world.addEntityToWorld(-100, fakePlayer);
    }

    @Override
    public void onDisable() {
        mc.world.removeEntityFromWorld(-100);
    }

    @Override
    public String getModuleInfo() {
        return customMode.getValue() ? customName : mode.getValue();
    }

}
