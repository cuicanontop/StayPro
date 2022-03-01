package dev.cuican.staypro.tasks;

import dev.cuican.staypro.client.ConfigManager;
import dev.cuican.staypro.concurrent.task.VoidTask;

/**
 * Created by B_312 on 05/01/2021
 */
public class ConfigOperateTask implements VoidTask {

    public Operation operation;

    public ConfigOperateTask(Operation operation) {
        this.operation = operation;
    }

    @Override
    public void invoke() {
        switch (operation) {
            case Save: {
                ConfigManager.saveAll();
                break;
            }
            case Load: {
                ConfigManager.safeLoadAll();
                break;
            }
        }
    }

    public enum Operation {
        Save,
        Load
    }

}
