package execution;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public final class ScheduledExecutorSpec {

    private final int corePoolSize = 1;
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(corePoolSize);
    private final Runnable runner = () -> {};
    private final List<Runnable> runners = Collections.singletonList(runner);
    private final ScheduledExecutor scheduledExecutor = new ScheduledExecutor(executor, runners);

    @Test
    public void whenGetTasksThenSameSizeAsRunners() {
        assertThat(scheduledExecutor.getTasks().size(), is(runners.size()));
    }

    @Test
    public void whenNewRunnerAddedThenTasksUpdated() throws Exception {
        final Runnable newRunner = () -> {};
        scheduledExecutor.addRunnable(newRunner);
        assertThat(scheduledExecutor.getTasks().size(), is(2));
        scheduledExecutor.addRunnable(newRunner, 1L, 30L, TimeUnit.HOURS);
        assertThat(scheduledExecutor.getTasks().size(), is(3));
    }

    @Test
    public void whenConstructedWithNoRunnersThenAllTaskFailedIsFalse() {
        ScheduledExecutor scheduledExecutor = new ScheduledExecutor(executor, new ArrayList<>());
        assertThat(scheduledExecutor.allTasksRemoved(), is(false));
    }

    @Test
    public void whenConstructedWithSomeRunnersThenAllTasksFailedIsFalse() {
        assertThat(scheduledExecutor.allTasksRemoved(), is(false));
    }

    @Test
    public void whenAllTasksFailThenAllTasksRemovedByMonitor() throws Exception {
        Runnable failingRunner = () -> {throw new RuntimeException();};
        ScheduledExecutor scheduledExecutor = new ScheduledExecutor(executor, new ArrayList<>());
        scheduledExecutor.addRunnable(failingRunner, 0L, 1L, TimeUnit.MILLISECONDS);
        Thread.sleep(1L); //Give the task time to fail.
        Runnable monitor = new ScheduledExecutor.Monitor(scheduledExecutor);
        monitor.run();
        assertThat(scheduledExecutor.allTasksRemoved(), is(true));
    }
}
