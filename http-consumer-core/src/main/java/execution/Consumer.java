package execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The primary consumption runner. The application takes source URLs as input and outputs the body of
 * the http response to some destination. It does so by executing a collection of runnables, each runnable
 * corresponding to one source and one destination. Given a source and other metadata, the runnable
 * creates a unique destination to save the data to with each run.
 */
final class Consumer {

    private static final int MAX_CORE_POOL_SIZE = 10;

    private final ScheduledThreadPoolExecutor executorService;
    private final AtomicReference<List<Runnable>> runners;
    private final AtomicReference<List<ScheduledFuture<?>>> tasks;
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private final long defaultInitialDelay = 1L;
    private final long defaultDelay = 30L;
    private final TimeUnit defaultTimeUnit = TimeUnit.SECONDS;


    Consumer(ScheduledThreadPoolExecutor executorService, List<Runnable> runners) {
        this.executorService = executorService;
        this.runners = new AtomicReference<>(new ArrayList<>(runners));
        this.tasks = new AtomicReference<>(new ArrayList<>(this.runners.get().size()));
        if (this.runners.get().size() > 0) {
            this.isInitialized.set(true);
        }
        for (Runnable runner : runners) {
            tasks.get().add(executorService.scheduleWithFixedDelay(runner, defaultInitialDelay, defaultDelay, defaultTimeUnit));
        }
    }

    boolean allTasksRemoved() {
        return this.isInitialized.get() && this.tasks.get().isEmpty();
    }

    List<ScheduledFuture<?>> getTasks() {
        return this.tasks.get();
    }

    void addRunnable(Runnable runnable, final long initialDelay, final long delay, final TimeUnit timeUnit) {
        final int currentPoolSize = this.executorService.getCorePoolSize();
        if (this.executorService.getCorePoolSize() < MAX_CORE_POOL_SIZE) {
            this.executorService.setCorePoolSize(currentPoolSize + 1);
        }
        this.runners.get().add(runnable);
        this.isInitialized.set(true);
        this.tasks.get().add(this.executorService.scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit));
    }

    void addRunnable(Runnable runnable) {
        addRunnable(runnable, defaultInitialDelay, defaultDelay, defaultTimeUnit);
    }

    static final class Monitor implements Runnable {

        private static final Logger logger = LoggerFactory.getLogger(Monitor.class);

        private final Consumer consumer;

        Monitor(final Consumer consumer) {
            this.consumer = consumer;
        }

        @Override
        public void run() {
            if (consumer.allTasksRemoved()) {
                logger.warn("There are no tasks available to execute.");
            } else {
                Collection<ScheduledFuture<?>> markedForRemoval = new ArrayList<>();
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
}
