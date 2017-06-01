package http.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

final class ConsumerMonitor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerMonitor.class);

    private final Consumer consumer;

    ConsumerMonitor(final Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        if (consumer.allTasksFailed()) {
            logger.warn("There are no tasks available to execute.");
        } else {
            List<ScheduledFuture<?>> markedForRemoval = new ArrayList<>();
            for (ScheduledFuture<?> task : consumer.getTasks()) {
                if (task.isDone()) {
                    logger.error("Unexpected error during task execution. The task will no longer run.");
                    markedForRemoval.add(task);
                }
            }
            for (ScheduledFuture<?> task : markedForRemoval) {
                task.cancel(true);
            }
            consumer.getTasks().removeAll(markedForRemoval);
        }
    }
}
