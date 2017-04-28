package http.execution;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Wraps a {@link ScheduledExecutorService} to add functionality for catching and logging exceptions thrown by the
 * executor service. See <a target="_blank"
 * href="http://code.nomad-labs.com/2011/12/09/mother-fk-the-scheduledexecutorservice/">
 * this article.</a>
 */
public final class LoggedScheduledExecutor {

    //private static final Logger LOGGER = LoggerFactory.getLogger(LoggedScheduledExecutor.class);
    private final ScheduledExecutorService executorService;

    public LoggedScheduledExecutor(@NonNull ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    ScheduledExecutorService getExecutorService() {
        return this.executorService;
    }

    public ScheduledFuture<?> execute(@NonNull Runnable command, long initialDelay, long delay, TimeUnit timeUnit) {
        Runnable loggedCommand = new LoggedRunnable(command);
        return executorService.scheduleWithFixedDelay(loggedCommand, initialDelay, delay, timeUnit);
    }

    static Runnable newLoggedRunnable(Runnable runnable) {
        return new LoggedRunnable(runnable);
    }

    private static final class LoggedRunnable implements Runnable {

        private static final Logger LOGGER = LoggerFactory.getLogger(LoggedRunnable.class);
        private final Runnable wrappedRunnable;

        LoggedRunnable(Runnable wrappedRunnable) {
            this.wrappedRunnable = wrappedRunnable;
        }

        @Override
        public void run() {
            try {
                wrappedRunnable.run();
            } catch (Exception e) {
                LOGGER.error("The schedule executor service runnable threw an exception.", e);
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LoggedRunnable runnable = (LoggedRunnable) o;

            return this.wrappedRunnable.equals(runnable.wrappedRunnable);
        }

        @Override
        public int hashCode() {
            return this.wrappedRunnable.hashCode();
        }

        @Override
        public String toString() {
            return "LoggedRunnable{" + "wrappedRunnable=" + this.wrappedRunnable + '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoggedScheduledExecutor executor = (LoggedScheduledExecutor) o;

        return this.executorService.equals(executor.executorService);
    }

    @Override
    public int hashCode() {
        return this.executorService.hashCode();
    }

    @Override
    public String toString() {
        return "LoggedScheduledExecutor{" + "executorService=" + this.executorService + '}';
    }
}
