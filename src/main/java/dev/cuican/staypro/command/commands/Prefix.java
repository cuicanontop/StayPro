package dev.cuican.staypro.command.commands;


import dev.cuican.staypro.client.CommandManager;
import dev.cuican.staypro.command.Command;
import dev.cuican.staypro.common.annotations.CommandInfo;
import dev.cuican.staypro.utils.ChatUtil;
import dev.cuican.staypro.utils.SoundUtil;

/**
 * Created by killRED on 2020
 * Updated by B_312 on 01/15/21
 */
@CommandInfo(command = "prefix", description = "Set command prefix.")
public class Prefix extends Command {

    @Override
    public void onCall(String s, String[] args) {
        if (args.length <= 0) {
            ChatUtil.sendNoSpamErrorMessage("Please specify a new prefix!");
            return;
        }
        if (args[0] != null) {
            CommandManager.cmdPrefix = args[0];
            ChatUtil.sendNoSpamMessage("Prefix set to " + ChatUtil.SECTIONSIGN + "b" + args[0] + "!");
            SoundUtil.playButtonClick();
        }
    }

    @Override
    public String getSyntax() {
        return "prefix <char>";
    }

}
