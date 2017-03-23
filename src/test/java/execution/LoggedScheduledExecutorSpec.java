package execution;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.concurrent.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class LoggedScheduledExecutorSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ScheduledExecutorService executorService = mock(ScheduledExecutorService.class);
    private LoggedScheduledExecutor executor = new LoggedScheduledExecutor(executorService);
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    private Runnable command = () -> {};
    private long initialDelay = 1L;
    private long delay = 1L;

    @Test
    public void whenInstantiatedThenScheduledExecutorExists() {
        assertThat(executor.getExecutorService(), is(executorService));
    }

    @Test
    public void whenExecuteThenScheduleWithFixedDelay() {
        executor.execute(command, initialDelay, delay, timeUnit);
        Runnable runnable = LoggedScheduledExecutor.newLoggedRunnable(command);
        verify(executorService).scheduleWithFixedDelay(runnable, initialDelay, delay, timeUnit);
    }

    @Test
    public void whenRunnableHasExceptionThenRuntimeException() {
        command = mock(Runnable.class);
        doThrow(ExecutionException.class).when(command).run();
        executorService = Executors.newSingleThreadScheduledExecutor();
        executor = new LoggedScheduledExecutor(executorService);
        ScheduledFuture<?> future = executor.execute(command, initialDelay, delay, timeUnit);
        try {
            future.get();
        } catch (Exception e) {
            assertThat(e.getCause(), is(instanceOf(RuntimeException.class)));
        }
    }

    @Test
    public void whenNullExecutorServiceThenNPE() {
        exception.expect(NullPointerException.class);
        executor = new LoggedScheduledExecutor(null);
    }

    @Test
    public void whenNullCommandExecuteThenNPE() {
        exception.expect(NullPointerException.class);
        executor.execute(null, initialDelay, delay, timeUnit);
    }

    @Test
    public void whenRunThenWrappedRunnableRan() {
        command = mock(Runnable.class);
        Runnable loggedRunnable = LoggedScheduledExecutor.newLoggedRunnable(command);
        loggedRunnable.run();
        verify(command).run();

    }
}
