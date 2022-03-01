package dev.cuican.staypro.command.commands;

import dev.cuican.staypro.command.Command;
import dev.cuican.staypro.common.annotations.CommandInfo;
import dev.cuican.staypro.tasks.Tasks;
import dev.cuican.staypro.utils.ChatUtil;

import static dev.cuican.staypro.concurrent.TaskManager.launch;

/**
 * Created by killRED on 2020
 * Updated by B_312 on 01/15/21
 */
@CommandInfo(command = "config", description = "Save or load config.")
public class Config extends Command {

    @Override
    public void onCall(String s, String[] args) {
        if (args[0] == null) {
            ChatUtil.sendNoSpamMessage("Missing argument &bmode&r: Choose from reload, save or path");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "save":
                this.save();
                break;
            case "load":
                this.load();
                break;
        }
    }

    @Override
    public String getSyntax() {
        return "config <save/load>";
    }

    public void load() {
        launch(Tasks.LoadConfig);
        ChatUtil.sendNoSpamMessage("Configuration reloaded!");
    }

    public void save() {
        launch(Tasks.SaveConfig);
        ChatUtil.sendNoSpamMessage("Configuration saved!");
    }

}
