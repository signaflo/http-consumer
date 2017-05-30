package execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * The primary application thread. The application takes source URLs as input and outputs the body of
 * the http response to some destination. It does so by executing a collection of runnables, each runnable
 * corresponding to one source and one destination. Given a source and other metadata, the runnable
 * creates a unique destination to save the data to with each run.
 */
public class ConsumerThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerThread.class);

    private final ScheduledExecutorService executorService;
    private final List<Runnable> runners;
    private boolean allTasksFailed = false;

    ConsumerThread(ScheduledExecutorService executorService, List<Runnable> runners) {
        this.executorService = executorService;
        this.runners = new ArrayList<>(runners);
    }

    boolean didAllTasksFail() {
        return allTasksFailed;
    }

    @Override
    public void run() {
        final long initialDelay = 1;
        final long delay = 30;
        final TimeUnit timeUnit = TimeUnit.SECONDS;
        List<ScheduledFuture<?>> tasks = new ArrayList<>(4);
        for (Runnable runner : runners) {
            tasks.add(executorService.scheduleWithFixedDelay(runner, initialDelay, delay, timeUnit));
        }
        while (!allTasksFailed) {
            if (tasks.isEmpty()) {
                logger.error("No more tasks remaining to execute.");
                allTasksFailed = true;
                break;
            }
            List<ScheduledFuture<?>> markedForRemoval = new ArrayList<>(runners.size());
            for (ScheduledFuture<?> task : tasks) {
                if (task.isDone()) {
                    logger.error("Unexpected error during task execution. Task will no longer run.");
                    markedForRemoval.add(task);
                }
            }
            for (ScheduledFuture<?> task : markedForRemoval) {
                task.cancel(true);
            }
            tasks.removeAll(markedForRemoval);
            try {
                Thread.sleep(15 * 1000L);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    public void addRunnable(Runnable runnable) {
        this.runners.add(runnable);
    }
}
