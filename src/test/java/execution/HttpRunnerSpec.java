package execution;

import org.junit.Ignore;
import org.junit.Test;

public class HttpRunnerSpec {

    @Test
    @Ignore
    public void quickTest() throws Exception {
        String url = "https://data.austintexas.gov/download/cuc7-ywmd/text/plain";
        HttpRunner runner = new HttpRunner(url);
        runner.run();
        Thread.sleep(15000L);
        runner.run();
        Thread.sleep(15000L);
        runner.run();
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//        executorService.scheduleWithFixedDelay(repeatRequest, 1, 15, TimeUnit.SECONDS);
//        Thread.sleep(100 * 1000);
    }
}

