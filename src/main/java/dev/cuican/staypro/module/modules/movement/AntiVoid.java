//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

// 
// Decompiled by Procyon v0.5.36
// 

package dev.cuican.staypro.module.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.cuican.staypro.common.annotations.ModuleInfo;
import dev.cuican.staypro.concurrent.event.Listener;
import dev.cuican.staypro.event.events.client.MoveEvent;
import dev.cuican.staypro.module.Category;
import dev.cuican.staypro.module.Module;
import dev.cuican.staypro.setting.Setting;
import dev.cuican.staypro.utils.ChatUtil;

@ModuleInfo(name = "AntiVoid", category = Category.MOVEMENT, description = "Fuck OFF 2b2t.org void")
public class AntiVoid extends Module {
    public Setting<String> mode;
    public Setting<Boolean> display;

    public AntiVoid() {
        mode = setting("Mode", "BOUNCE",listOf(  "BOUNCE", "CANCEL"));
        display = setting("WarnMessage",true);
    }

    @Listener
    public void onUpdate(MoveEvent event) {
        final double yLevel = mc.player.posY;
        if (yLevel <= 0.5) {
            ChatUtil.sendMessage(ChatFormatting.RED + "Player " + ChatFormatting.GREEN + mc.player.getName() + ChatFormatting.RED + " is in the void!");
            if (this.mode.getValue().equals("BOUNCE")) {
                mc.player.moveVertical = 10.0f;
                mc.player.jump();
            }
            if (this.mode.getValue().equals("CANCEL")) {
                mc.player.jump();
               event.setCanceled(true);
            }
        } else {
            mc.player.moveVertical = 0.0f;
        }
    }

    @Override
    public void onDisable() {
        mc.player.moveVertical = 0.0f;
    }

    @Override
    public String getModuleInfo() {
        if (this.display.getValue()) {
            if (this.mode.getValue().equals("BOUNCE")) {
                return "Bounce";
            }
            if (this.mode.getValue().equals("CANCEL")) {
                return "Cancel";
            }
        }
        return "";
    }


}
