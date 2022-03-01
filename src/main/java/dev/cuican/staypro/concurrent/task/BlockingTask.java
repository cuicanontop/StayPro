package dev.cuican.staypro.concurrent.task;

import dev.cuican.staypro.concurrent.thread.BlockingContent;

public interface BlockingTask {
    void invoke(BlockingContent unit);
}
