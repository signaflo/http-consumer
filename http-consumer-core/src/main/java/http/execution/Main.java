package http.execution;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The entry point for the application.
 */
public final class Main {

    private static AtomicBoolean stopRequested = new AtomicBoolean(false);

    /**
     * The method that starts application execution.
     *
     * @param args arguments provided at startup.
     */
    public static void main(String... args) throws InterruptedException {
        //new ConsumerExecutor();
        Thread backgroundThread = new Thread(() -> {
            long i = 0;
            while (!stopRequested.get()) {
                i++;
            }
        });
        backgroundThread.start();
        TimeUnit.SECONDS.sleep(1);
        stopRequested.set(true);
    }
}
