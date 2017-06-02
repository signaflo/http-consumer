package execution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The gateway to the consumer functionality, this class is responsible for setting up and initializing the various
 * thread pools that execute and monitor the application logic.
 *
 * @author Jacob Rachiele
 *         Apr. 29, 2017
 */
final class ConsumerExecutor {

    private final Consumer consumer;

    private ConsumerExecutor(final int initialCores, final List<Runnable> initialRunners) {
        ScheduledExecutorService consumerMonitorService = Executors.newSingleThreadScheduledExecutor();
        ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(initialCores);
        this.consumer = new Consumer(executorService, initialRunners);
        Runnable consumerMonitor = new Consumer.Monitor(consumer);
        long initialDelay = 1L;
        long monitorInterval = 60 * 60L;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        consumerMonitorService.scheduleWithFixedDelay(consumerMonitor, initialDelay, monitorInterval, timeUnit);
    }

    ConsumerExecutor() {
        this(0, new ArrayList<>());
    }

    Consumer getConsumer() {
        return this.consumer;
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

//    private static List<Runnable> getRunners() {
//
//        final String vehiclePositionsJsonURL = "https://data.austintexas.gov/download/cuc7-ywmd/text/plain";
//        final String tripUpdatesJsonURL = "https://data.texas.gov/download/mqtr-wwpy/text%2Fplain";
//        final String vehiclePositionsPbURL = "https://data.texas.gov/download/eiei-9rpf/application%2Foctet-stream";
//        final String tripUpdatesPbURL = "https://data.texas.gov/download/rmk2-acnw/application%2Foctet-stream";
//
//        final String vehiclePositionsPrefix = "vehicle-positions";
//        final String tripUpdatesPrefix = "trip-updates";
//        final String jsonDirectory = "data" + File.separator + "json";
//        final String pbDirectory = "data" + File.separator + "pb";
//        final String jsonSuffix = "json";
//        final String pbSuffix = "pb";
//        final String jsonContentType = "application/octet-stream";
//        final String pbContentType = "application/octet-stream";
//
//        Map<String, String> requestProperties = new HashMap<>(3);
//        requestProperties.put("Content-Type", jsonContentType);
//        requestProperties.put("X-App-Token", "b7mZs9To48yt7Lver4EABPq0j");
//
//        PathProperties pathProperties = new PathProperties(vehiclePositionsPrefix, jsonSuffix,
//                                                           jsonDirectory + File.separator + vehiclePositionsPrefix);
//        Runnable vehiclePositionsJsonRunner = new HttpDailyRunner(vehiclePositionsJsonURL, requestProperties,
//                                                                  pathProperties);
//
//        pathProperties = new PathProperties(tripUpdatesPrefix, jsonSuffix,
//                                            jsonDirectory + File.separator + tripUpdatesPrefix);
//        Runnable tripUpdatesJsonRunner = new HttpDailyRunner(tripUpdatesJsonURL, requestProperties,
//                                                             pathProperties);
//
//        requestProperties.put("Content-Type", pbContentType);
//
//        pathProperties = new PathProperties(vehiclePositionsPrefix, pbSuffix,
//                                            pbDirectory + File.separator + vehiclePositionsPrefix);
//        Runnable vehiclePositionPbRunner = new HttpDailyRunner(vehiclePositionsPbURL, requestProperties,
//                                                               pathProperties);
//
//        pathProperties = new PathProperties(tripUpdatesPrefix, pbSuffix,
//                                            pbDirectory + File.separator + tripUpdatesPrefix);
//        Runnable tripUpdatesPbRunner = new HttpDailyRunner(tripUpdatesPbURL, requestProperties,
//                                                           pathProperties);
//
//        return Arrays.asList(/*vehiclePositionsJsonRunner/*, tripUpdatesJsonRunner,
//                             vehiclePositionPbRunner, */tripUpdatesPbRunner);
//    }
}
