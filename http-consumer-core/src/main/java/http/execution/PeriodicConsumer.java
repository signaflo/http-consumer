package http.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The primary consumption runner. The application takes source URLs as input and outputs the body of
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
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private final long initialDelay = 1L;
    private final long delay = 30L;
    private final TimeUnit timeUnit = TimeUnit.SECONDS;


    PeriodicConsumer(ScheduledThreadPoolExecutor executorService, List<Runnable> runners) {
        this.executorService = executorService;
        this.runners = new ArrayList<>(runners);
        this.tasks = new ArrayList<>(this.runners.size());
        if (this.runners.size() > 0) {
            this.isInitialized.set(true);
        }
        for (Runnable runner : runners) {
            tasks.add(executorService.scheduleWithFixedDelay(runner, initialDelay, delay, timeUnit));
        }
    }

    synchronized boolean allTasksFailed() {
        return this.isInitialized.get() && this.tasks.isEmpty();
    }

    @Override
    public synchronized void run() {
        if (allTasksFailed()) {
            logger.warn("There are no tasks remaining to execute.");
        } else {
            List<ScheduledFuture<?>> markedForRemoval = new ArrayList<>(runners.size());
            for (ScheduledFuture<?> task : tasks) {
                if (task.isDone()) {
                    logger.error("Unexpected error during task execution. The task will no longer run.");
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
        this.isInitialized.set(true);
        final int currentPoolSize = this.executorService.getCorePoolSize();
        if (this.executorService.getCorePoolSize() < MAX_CORE_POOL_SIZE) {
            this.executorService.setCorePoolSize(currentPoolSize + 1);
        }
        tasks.add(executorService.scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit));
    }
}
