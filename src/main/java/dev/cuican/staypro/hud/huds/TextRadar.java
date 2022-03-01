package dev.cuican.staypro.hud.huds;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.cuican.staypro.client.FontManager;
import dev.cuican.staypro.client.FriendManager;
import dev.cuican.staypro.client.GUIManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.engine.AsyncRenderer;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.utils.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

@ModuleInfo(name = "TextRadar", category = Category.HUD)
public class TextRadar extends HUDModule {

    DecimalFormat dfHealth;
    StringBuilder healthSB = new StringBuilder();



    String viewText = "";
    int DefaultWidth = 75;

    @Override
    public void onHUDEnable()  {
        dfHealth = new DecimalFormat("#.#");
        dfHealth.setRoundingMode(RoundingMode.HALF_UP);
    }


    @Override
    public void onHUDRender(ScaledResolution resolution) {
        if (mc.player == null || mc.world == null||isDisabled()) {
            return;
        }


            viewText = "";
            List<EntityPlayer> entityList = mc.world.playerEntities;
            Map<String, Integer> players = new HashMap<>();
            for (EntityPlayer e : entityList) {
                if (e.getName().equals(mc.player.getName())) {
                    continue;
                }
                String posString = (e.posY > mc.player.posY ? ChatFormatting.DARK_GREEN + "+ " : (e.posY == mc.player.posY ? " " : ChatFormatting.DARK_RED + "- "));

                String strengthfactor = "";
                if (e.isPotionActive(MobEffects.STRENGTH)) {
                    strengthfactor = "S";
                }
                float hpRaw = e.getHealth() + ((EntityLivingBase) e).getAbsorptionAmount();

                String hp = dfHealth.format(hpRaw);
                healthSB.append(ChatUtil.SECTIONSIGN);
                if (hpRaw >= 20) {
                    healthSB.append("a");
                } else if (hpRaw >= 10) {
                    healthSB.append("e");
                } else if (hpRaw >= 5) {
                    healthSB.append("6");
                } else {
                    healthSB.append("c");
                }
                healthSB.append(hp);
                players.put(ChatFormatting.AQUA + posString + "Player " + healthSB + " " + ChatFormatting.RED + strengthfactor + (strengthfactor.equals("S") ? " " : "") + (FriendManager.isFriend(e.getName()) ? ChatFormatting.GREEN : ChatFormatting.DARK_BLUE) + e.getName(), (int) mc.player.getDistance(e));
                healthSB.setLength(0);


            if (players.isEmpty()) {
                viewText = "";
                return;
            }

            players = sortByValue(players);

            for (Map.Entry<String, Integer> player : players.entrySet()) {
                addLine(ChatUtil.SECTIONSIGN + "7" + player.getKey() + " " + ChatUtil.SECTIONSIGN + "4" + player.getValue());
            }
        }

        String[] mutliLineText = viewText.split("\n");
        int addY = 0;
        int maxFontWidth = DefaultWidth;
        for (String textt : mutliLineText) {
            FontManager.fontRenderer.drawString(textt, x, y + addY, Color.WHITE.getRGB());
            maxFontWidth = Math.max(maxFontWidth, mc.fontRenderer.getStringWidth(textt));
            addY += mc.fontRenderer.FONT_HEIGHT;
        }
        if (addY ==mc.fontRenderer.FONT_HEIGHT) {
            height = mc.fontRenderer.FONT_HEIGHT;
        } else {
            height = addY - mc.fontRenderer.FONT_HEIGHT;
        }
        width = maxFontWidth;
    }

    private void addLine(String str) {
        if (viewText.isEmpty()) {
            viewText = str;
        } else {
            viewText = viewText + "\n" + str;
        }
    }



    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}
