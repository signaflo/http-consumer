package http.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The primary application thread. The application takes source URLs as input and outputs the body of
 * the http response to some destination. It does so by executing a collection of runnables, each runnable
 * corresponding to one source and one destination. Given a source and other metadata, the runnable
 * creates a unique destination to save the data to with each run.
 */
public class PeriodicConsumer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicConsumer.class);
    private static final int MAX_CORE_POOL_SIZE = 10;

    private final ScheduledThreadPoolExecutor executorService;
    private final List<Runnable> runners;
    private final List<ScheduledFuture<?>> tasks;
    private AtomicBoolean allTasksFailed = new AtomicBoolean(false);
    private AtomicBoolean isInitialized = new AtomicBoolean(false);
    private final long initialDelay = 1L;
    private final long delay = 30L;
    private final TimeUnit timeUnit = TimeUnit.SECONDS;


    PeriodicConsumer(ScheduledThreadPoolExecutor executorService, List<Runnable> runners) {
        this.executorService = executorService;
        this.runners = new ArrayList<>(runners);
        if (this.runners.size() > 0) {
            this.isInitialized.set(true);
        }
        this.tasks = new ArrayList<>(this.runners.size());
        for (Runnable runner : runners) {
            tasks.add(executorService.scheduleWithFixedDelay(runner, initialDelay, delay, timeUnit));
        }
    }

    synchronized boolean didAllTasksFail() {
        return this.allTasksFailed.get();
    }

    @Override
    public void run() {
        if (this.runners.size() > 0) {
            this.isInitialized.set(true);
        }
        while (isInitialized.get() && !allTasksFailed.get()) {
            if (tasks.isEmpty()) {
                logger.error("No more tasks remaining to execute.");
                allTasksFailed.set(true);
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
        }
    }

    synchronized void addRunnable(Runnable runnable) {
        this.runners.add(runnable);
        final int currentPoolSize = this.executorService.getCorePoolSize();
        if (this.executorService.getCorePoolSize() < MAX_CORE_POOL_SIZE) {
            this.executorService.setCorePoolSize(currentPoolSize + 1);
        }
        tasks.add(executorService.scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit));
        this.allTasksFailed.set(false);
    }
}
