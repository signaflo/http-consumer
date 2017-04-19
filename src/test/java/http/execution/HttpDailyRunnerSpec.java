package http.execution;

import com.google.protobuf.util.JsonFormat;
import com.google.transit.realtime.GtfsRealtime;
import http.data.ContentType;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpDailyRunnerSpec {

    @Test
    @Ignore
    public void quickTest() throws Exception {
        //String url = "https://data.texas.gov/download/cuc7-ywmd/text/plain";
        String url = "https://data.texas.gov/download/eiei-9rpf/application/octet-stream";
        //String contentType = "text/plain; charset=UTF-8";
        String contentType = "application/octet-stream";
        String fileSuffix = "pb";
        Map<String, String> requestProperties = new HashMap<>(3);
        requestProperties.put("Content-Type", contentType);
        requestProperties.put("X-App-Token", "b7mZs9To48yt7Lver4EABPq0j");
        HttpDailyRunner runner = new HttpDailyRunner(url, "data/" + fileSuffix, "VehiclePositions",
                                                     fileSuffix, requestProperties, ContentType.BINARY);
        URL resource = new URL(url);
        GtfsRealtime.FeedMessage message = GtfsRealtime.FeedMessage.parseFrom(resource.openStream());
        for (GtfsRealtime.FeedEntity entity : message.getEntityList()) {
            if (entity.hasVehicle()) {
                System.out.println(JsonFormat.printer().print(entity.getVehicleOrBuilder()));
            }
        }
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

