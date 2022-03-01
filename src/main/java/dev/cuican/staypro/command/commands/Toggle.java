package dev.cuican.staypro.command.commands;

import dev.cuican.staypro.client.ModuleManager;
import dev.cuican.staypro.command.Command;
import dev.cuican.staypro.common.annotations.CommandInfo;
import dev.cuican.staypro.utils.ChatUtil;

import java.util.Objects;

/**
 * Created by killRED on 2020
 * Updated by B_312 on 01/15/21
 */
@CommandInfo(command = "toggle",description = "Toggle selected module or HUD.")
public class Toggle extends Command {

    @Override
    public void onCall(String s, String[] args) {
        try {
            Objects.requireNonNull(ModuleManager.getModuleByName(args[0])).toggle();
        } catch(Exception e) {
            ChatUtil.sendNoSpamErrorMessage(getSyntax());
        }
    }

    @Override
    public String getSyntax() {
        return "toggle <modulename>";
    }

}