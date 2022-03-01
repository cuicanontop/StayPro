package dev.cuican.staypro.tasks;

import dev.cuican.staypro.concurrent.task.VoidTask;

public enum Tasks {

    LoadConfig(new ConfigOperateTask(ConfigOperateTask.Operation.Load)),
    SaveConfig(new ConfigOperateTask(ConfigOperateTask.Operation.Save));

    public VoidTask task;

    Tasks(VoidTask task) {
        this.task = task;
    }

}