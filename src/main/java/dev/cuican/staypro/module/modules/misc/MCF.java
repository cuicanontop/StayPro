package dev.cuican.staypro.module.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.cuican.staypro.client.FriendManager;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.module.Category;

import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.utils.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Mouse;
@ModuleInfo(name = "MCF", category = Category.MISC,description = "Middleclick Friends.")
public class MCF extends Module {
    private boolean clicked = false;


    @Override
    public void onTick() {
        if (Mouse.isButtonDown(2)) {
            if (!clicked && MCF.mc.currentScreen == null) {
                onClick();
            }
            clicked = true;
        } else {
            clicked = false;
        }
    }

    private void onClick() {
        Entity entity;
        RayTraceResult result = MCF.mc.objectMouseOver;
        if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && (entity = result.entityHit) instanceof EntityPlayer) {
            if (FriendManager.isFriend(entity.getName())) {
                FriendManager.remove(entity.getName());
                ChatUtil.printChatMessage(ChatFormatting.RED + entity.getName() + ChatFormatting.RED + " has been unfriended.");
            } else {
                FriendManager.add(entity.getName());
                ChatUtil.printChatMessage(ChatFormatting.AQUA + entity.getName() + ChatFormatting.AQUA + " has been friended.");
            }
        }
        clicked = true;
    }
}

