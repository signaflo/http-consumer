package execution;

import http.data.PathProperties;
import http.execution.HttpDailyRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The main application class.
 *
 * @author Jacob Rachiele
 *         Apr. 29, 2017
 */
public class ConsumerThreadController implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerThreadController.class);
    private static final int MONITOR_INTERVAL_MILLIS = 1000 * 5; // 5 minutes.
    private static final int MAXIMUM_RESTARTS = 5;

    @Override
    public void run() {
        Properties properties = getProperties();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
        ConsumerThread consumerThread = new ConsumerThread(executorService, getRunners());
        consumerThread.start();
        ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();
        MonitorThread monitorThread = new MonitorThread(consumerThread, executorService);
        monitor.scheduleWithFixedDelay(monitorThread, 100L,
                                               MONITOR_INTERVAL_MILLIS, TimeUnit.MILLISECONDS);
    }

    private class MonitorThread extends Thread {

        private int numRestarts = 0;
        private ConsumerThread consumerThread;
        private ScheduledExecutorService executorService;

        MonitorThread(ConsumerThread consumerThread, ScheduledExecutorService executorService) {
            this.consumerThread = consumerThread;
            this.executorService = executorService;
        }

        @Override
        public void run() {
            if (numRestarts >= MAXIMUM_RESTARTS) {
                logger.error("Maximum number of application restarts, " + MAXIMUM_RESTARTS + ", exceeded." +
                             " Shutting down JVM.");
                executorService.shutdown();
                System.exit(1);
            }
            if (consumerThread.didAllTasksFail()) {
                consumerThread = new ConsumerThread(executorService, getRunners());
                consumerThread.start();
                numRestarts++;
            }
        }
    }

    private Properties getProperties() {
        Path path = FileSystems.getDefault().getPath("etc", "capmetro.properties");
        try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.ISO_8859_1)) {
            Properties properties = new Properties();
            properties.load(reader);
            return properties;
        } catch (IOException ie) {
            logger.error("Could not create reader at path " + path);
            throw new RuntimeException(ie);
        }
    }

    private List<Runnable> getRunners() {

        final String vehiclePositionsJsonURL = "https://data.austintexas.gov/download/cuc7-ywmd/text/plain";
        final String tripUpdatesJsonURL = "https://data.texas.gov/download/mqtr-wwpy/text%2Fplain";
        final String vehiclePositionsPbURL = "https://data.texas.gov/download/eiei-9rpf/application%2Foctet-stream";
        final String tripUpdatesPbURL = "https://data.texas.gov/download/rmk2-acnw/application%2Foctet-stream";

        final String vehiclePositionsPrefix = "vehicle-positions";
        final String tripUpdatesPrefix = "trip-updates";
        final String jsonDirectory = "data" + File.separator + "json";
        final String pbDirectory = "data" + File.separator + "pb";
        final String jsonSuffix = "json";
        final String pbSuffix = "pb";
        final String jsonContentType = "application/octet-stream";
        final String pbContentType = "application/octet-stream";

        Map<String, String> requestProperties = new HashMap<>(3);
        requestProperties.put("Content-Type", jsonContentType);
        requestProperties.put("X-App-Token", "b7mZs9To48yt7Lver4EABPq0j");

        PathProperties pathProperties = new PathProperties(vehiclePositionsPrefix, jsonSuffix,
                                                           jsonDirectory + File.separator + vehiclePositionsPrefix);
        Runnable vehiclePositionsJsonRunner = new HttpDailyRunner(vehiclePositionsJsonURL, requestProperties,
                                                                  pathProperties);

        pathProperties = new PathProperties(tripUpdatesPrefix, jsonSuffix,
                                            jsonDirectory + File.separator + tripUpdatesPrefix);
        Runnable tripUpdatesJsonRunner = new HttpDailyRunner(tripUpdatesJsonURL, requestProperties,
                                                             pathProperties);

        requestProperties.put("Content-Type", pbContentType);

        pathProperties = new PathProperties(vehiclePositionsPrefix, pbSuffix,
                                            pbDirectory + File.separator + vehiclePositionsPrefix);
        Runnable vehiclePositionPbRunner = new HttpDailyRunner(vehiclePositionsPbURL, requestProperties,
                                                               pathProperties);

        pathProperties = new PathProperties(tripUpdatesPrefix, pbSuffix,
                                            pbDirectory + File.separator + tripUpdatesPrefix);
        Runnable tripUpdatesPbRunner = new HttpDailyRunner(tripUpdatesPbURL, requestProperties,
                                                           pathProperties);

        return Arrays.asList(vehiclePositionsJsonRunner/*, tripUpdatesJsonRunner,
                             vehiclePositionPbRunner, tripUpdatesPbRunner*/);
    }
}
