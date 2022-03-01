package dev.cuican.staypro.hud.huds;


import dev.cuican.staypro.client.FriendManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.engine.AsyncRenderer;
import dev.cuican.staypro.hud.HUDModule;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.utils.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(name="Friends", category = Category.HUD)
public class Friends extends HUDModule {

    String viewText = "";
    int DefaultWidth = 60;
    int DefaultHeight = 10;


    public Friends(){
        asyncRenderer = new AsyncRenderer(){

            @Override
            public void onUpdate(ScaledResolution resolution, int mouseX, int mouseY) {
                String[] mutliLineText = viewText.split("\n");
                int addY = 0;
                int maxFontWidth = DefaultWidth;
                for (String textt : mutliLineText) {
                    drawAsyncString(textt, x, y + addY, Color.WHITE.getRGB());
                    maxFontWidth = Math.max(maxFontWidth, mc.fontRenderer.getStringWidth(textt));
                    addY += mc.fontRenderer.FONT_HEIGHT;
                }
                if (addY == mc.fontRenderer.FONT_HEIGHT) {
                    height =mc.fontRenderer.FONT_HEIGHT;
                }else {
                    height = addY - mc.fontRenderer.FONT_HEIGHT;
                }
                width = maxFontWidth;
            }
        };
    }
    @Override
    public void onHUDRender(ScaledResolution resolution) {
        asyncRenderer.onRender();
    }


    @Override
    public void onTick() {
        viewText = "";
        if (getFriends().isEmpty()) {
            addLine("You have no friends!");
        } else {
            addLine(ChatUtil.SECTIONSIGN + "3" + ChatUtil.SECTIONSIGN + "l" + "Your Friends");
            Iterator var1 = getFriends().iterator();
            while (var1.hasNext()) {
                Entity e = (Entity) var1.next();
                addLine(ChatUtil.SECTIONSIGN + "6 " + e.getName());
            }
        }
    }

    private void addLine(String str) {
        if (viewText.isEmpty()) {
            viewText = str;
        } else {
            viewText = viewText + "\n" + str;
        }
    }


    public static List<EntityPlayer> getFriends() {
        return mc.world.playerEntities.stream().filter(entityPlayer -> FriendManager.isFriend(entityPlayer.getName())).collect(Collectors.toList());
    }
}
