package http.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
public final class Consumer {

    private static final int MAX_CORE_POOL_SIZE = 10;

    private final ScheduledThreadPoolExecutor executorService;
    private final AtomicReference<List<Runnable>> runners;
    private final AtomicReference<List<ScheduledFuture<?>>> tasks;
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);


    Consumer(ScheduledThreadPoolExecutor executorService, List<Runnable> runners) {
        this.executorService = executorService;
        this.runners = new AtomicReference<>(new ArrayList<>(runners));
        this.tasks = new AtomicReference<>(new ArrayList<>(this.runners.get().size()));
        if (this.runners.get().size() > 0) {
            this.isInitialized.set(true);
        }
        for (Runnable runner : runners) {
            long initialDelay = 1L;
            long delay = 30L;
            TimeUnit timeUnit = TimeUnit.SECONDS;
            tasks.get().add(executorService.scheduleWithFixedDelay(runner, initialDelay, delay, timeUnit));
        }
    }

    boolean allTasksFailed() {
        return /*this.isInitialized.get() && */this.tasks.get().isEmpty();
    }

    List<ScheduledFuture<?>> getTasks() {
        return this.tasks.get();
    }

    void addRunnable(Runnable runnable, final int initialDelay, final int delay, final TimeUnit timeUnit) {
        final int currentPoolSize = this.executorService.getCorePoolSize();
        if (this.executorService.getCorePoolSize() < MAX_CORE_POOL_SIZE) {
            this.executorService.setCorePoolSize(currentPoolSize + 1);
        }
        this.runners.get().add(runnable);
        this.isInitialized.set(true);
        this.tasks.get().add(this.executorService.scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit));
    }
}
