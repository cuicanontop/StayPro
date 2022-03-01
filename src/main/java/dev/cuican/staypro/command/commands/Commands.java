package dev.cuican.staypro.command.commands;


import dev.cuican.staypro.client.CommandManager;
import dev.cuican.staypro.command.Command;
import dev.cuican.staypro.common.annotations.CommandInfo;
import dev.cuican.staypro.utils.ChatUtil;

/**
 * Created by B_312 on 01/15/21
 */
@CommandInfo(command = "commands", description = "Lists all commands.")
public class Commands extends Command {

    @Override
    public void onCall(String s, String[] args) {
        ChatUtil.printChatMessage("\247b" + "Commands:");
        try {
            for (Command cmd : CommandManager.getInstance().commands) {
                if (cmd == this) {
                    continue;
                }
                ChatUtil.printChatMessage("\247b" + cmd.getSyntax().replace("<", "\2473<\2479").replace(">", "\2473>") + "\2478" + " - " + cmd.getDescription());
            }
        } catch (Exception e) {
            ChatUtil.sendNoSpamErrorMessage(getSyntax());
        }
    }

    @Override
    public String getSyntax() {
        return "commands";
    }

}