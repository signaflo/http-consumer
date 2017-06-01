package http.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class ConsumerMonitor extends Thread {

    private static final int MAXIMUM_RESTARTS = 5;
    private static final Logger logger = LoggerFactory.getLogger(ConsumerMonitor.class);

    private int numRestarts = 0;
    private PeriodicConsumer periodicConsumer;
    private final ScheduledThreadPoolExecutor executorService;
    private final List<Runnable> initialRunners;

    ConsumerMonitor(PeriodicConsumer periodicConsumer, ScheduledThreadPoolExecutor executorService,
                    List<Runnable> initialRunners) {
        this.periodicConsumer = periodicConsumer;
        this.executorService = executorService;
        this.initialRunners = new ArrayList<>(initialRunners);
    }

    @Override
    public void run() {
        if (numRestarts >= MAXIMUM_RESTARTS) {
            logger.error("Maximum number of application restarts, " + MAXIMUM_RESTARTS + ", exceeded." +
                         " Shutting down JVM.");
            executorService.shutdown();
            System.exit(1);
        }
        if (periodicConsumer.allTasksFailed()) {
            periodicConsumer = new PeriodicConsumer(executorService, new ArrayList<>(initialRunners));
            ScheduledExecutorService periodicRunner = Executors.newSingleThreadScheduledExecutor();
            periodicRunner.scheduleWithFixedDelay(periodicConsumer, 1L, 15L, TimeUnit.SECONDS);
            numRestarts++;
        }
    }
}
