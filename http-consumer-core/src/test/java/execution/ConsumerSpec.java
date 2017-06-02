package execution;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public final class ConsumerSpec {

    private final int corePoolSize = 1;
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(corePoolSize);
    private final Runnable runner = () -> {};
    private final List<Runnable> runners = Collections.singletonList(runner);
    private final Consumer consumer = new Consumer(executor, runners);

    @Test
    public void whenGetTasksThenSameSizeAsRunners() {
        assertThat(consumer.getTasks().size(), is(runners.size()));
    }

    @Test
    public void whenNewRunnerAddedThenTasksUpdated() throws Exception {
        final Runnable newRunner = () -> {};
        consumer.addRunnable(newRunner);
        assertThat(consumer.getTasks().size(), is(2));
        consumer.addRunnable(newRunner, 1L, 30L, TimeUnit.HOURS);
        assertThat(consumer.getTasks().size(), is(3));
    }

    @Test
    public void whenConstructedWithNoRunnersThenAllTaskFailedIsFalse() {
        Consumer consumer = new Consumer(executor, new ArrayList<>());
        assertThat(consumer.allTasksRemoved(), is(false));
    }

    @Test
    public void whenConstructedWithSomeRunnersThenAllTasksFailedIsFalse() {
        assertThat(consumer.allTasksRemoved(), is(false));
    }

    @Test
    public void whenAllTasksFailThenAllTasksRemovedByMonitor() throws Exception {
        Runnable failingRunner = () -> {throw new RuntimeException();};
        Consumer consumer = new Consumer(executor, new ArrayList<>());
        consumer.addRunnable(failingRunner, 0L, 1L, TimeUnit.MILLISECONDS);
        Thread.sleep(1L); //Give the task time to fail.
        Runnable monitor = new Consumer.Monitor(consumer);
        monitor.run();
        assertThat(consumer.allTasksRemoved(), is(true));
    }
}
