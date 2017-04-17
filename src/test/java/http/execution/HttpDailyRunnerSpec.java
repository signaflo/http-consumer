package http.execution;

import org.junit.Ignore;
import org.junit.Test;

public class HttpDailyRunnerSpec {

    @Test
    @Ignore
    public void quickTest() throws Exception {
        String url = "https://http.data.texas.gov/download/cuc7-ywmd/text/plain";
        //String url = "https://data.texas.gov/download/eiei-9rpf/application/octet-stream";
        String contentType = "text/plain; charset=UTF-8";
        //String contentType = "application/octet-stream";
        String fileSuffix = "json";
        HttpDailyRunner runner = new HttpDailyRunner(url, "data/" + fileSuffix,
                                                     "VehiclePositions", fileSuffix, contentType);
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

