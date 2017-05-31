package http.execution;

import http.data.PathProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The main application class.
 *
 * @author Jacob Rachiele
 *         Apr. 29, 2017
 */
public class PeriodicConsumerRunner implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PeriodicConsumerRunner.class);
    private static final long MONITOR_INTERVAL_MILLIS = 1000 * 30L; // 5 minutes.
    private static final int MAXIMUM_RESTARTS = 5;
    private final int initialCores;
    private final List<Runnable> initialRunners;

    private PeriodicConsumerRunner(final int initialCores, final List<Runnable> initialRunners) {
        this.initialCores = initialCores;
        this.initialRunners = new ArrayList<>(initialRunners);
    }

    PeriodicConsumerRunner() {
        this(4, getRunners());
    }

    @Override
    public void run() {
        //Properties properties = getProperties();
        ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(initialCores);
        PeriodicConsumer periodicConsumer = new PeriodicConsumer(executorService, getRunners());
        ScheduledExecutorService periodicRunner = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();
        MonitorThread monitorThread = new MonitorThread(periodicConsumer, executorService);
        periodicRunner.scheduleWithFixedDelay(periodicConsumer, 100L, 1000 * 10L, TimeUnit.MILLISECONDS);
        monitor.scheduleWithFixedDelay(monitorThread, 100L,
                                               MONITOR_INTERVAL_MILLIS, TimeUnit.MILLISECONDS);
//        try {
//            Thread.sleep(3000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        List<Runnable> runners = getRunners();
//        for (Runnable runner : runners) {
//            periodicConsumer.addRunnable(runner);
//        }
    }

    private class MonitorThread extends Thread {

        private int numRestarts = 0;
        private PeriodicConsumer periodicConsumer;
        private ScheduledThreadPoolExecutor executorService;

        MonitorThread(PeriodicConsumer periodicConsumer, ScheduledThreadPoolExecutor executorService) {
            this.periodicConsumer = periodicConsumer;
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
            if (periodicConsumer.didAllTasksFail()) {
                periodicConsumer = new PeriodicConsumer(executorService, new ArrayList<>(initialRunners));
                ScheduledExecutorService periodicRunner = Executors.newSingleThreadScheduledExecutor();
                periodicRunner.scheduleWithFixedDelay(periodicConsumer, 1L, 15L, TimeUnit.SECONDS);
                numRestarts++;
            }
        }
    }

//    private Properties getProperties() {
//        Path path = FileSystems.getDefault().getPath("etc", "capmetro.properties");
//        try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.ISO_8859_1)) {
//            Properties properties = new Properties();
//            properties.load(reader);
//            return properties;
//        } catch (IOException ie) {
//            logger.error("Could not create reader at path " + path);
//            throw new RuntimeException(ie);
//        }
//    }

    public static List<Runnable> getRunners() {

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

        return Arrays.asList(/*vehiclePositionsJsonRunner/*, tripUpdatesJsonRunner,
                             vehiclePositionPbRunner, */tripUpdatesPbRunner);
    }
}
