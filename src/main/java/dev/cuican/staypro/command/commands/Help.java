package dev.cuican.staypro.command.commands;

import dev.cuican.staypro.Stay;
import dev.cuican.staypro.client.CommandManager;
import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.command.Command;
import dev.cuican.staypro.common.annotations.CommandInfo;
import dev.cuican.staypro.module.modules.client.ClickGUI;
import dev.cuican.staypro.utils.ChatUtil;
import org.lwjgl.input.Keyboard;

/**
 * Created by B_312 on 01/15/21
 */
@CommandInfo(command = "help", description = "Get helps.")
public class Help extends Command {

    @Override
    public void onCall(String s, String[] args) {
        ChatUtil.printChatMessage("\247b" + Stay.MOD_NAME + " " + "\247a" + Stay.MOD_VERSION);
        ChatUtil.printChatMessage("\247c" + "Made by: " + Stay.AUTHOR);
        ChatUtil.printChatMessage("\247c" + "Github: " + Stay.GITHUB);
        ChatUtil.printChatMessage("\2473" + "Press " + "\247c" + Keyboard.getKeyName(ModuleManager.getModule(ClickGUI.class).keyBind.getKeyCode()) + "\2473" + " to open ClickGUI");
        ChatUtil.printChatMessage("\2473" + "Use command: " + "\2479" + CommandManager.cmdPrefix + "prefix <target prefix>" + "\2473" + " to set command prefix");
        ChatUtil.printChatMessage("\2473" + "List all available commands: " + "\2479" + CommandManager.cmdPrefix + "commands");
    }

    @Override
    public String getSyntax() {
        return "help";
    }

}