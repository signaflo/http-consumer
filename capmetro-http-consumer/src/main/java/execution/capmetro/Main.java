package execution.capmetro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main application class.
 *
 * @author Jacob Rachiele
 *         Apr. 29, 2017
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int MONITOR_INTERVAL_MILLIS = 1000 * 60 * 5; // 5 minutes.
    private static final int MAXIMUM_RESTARTS = 50;

    public static void main(String[] args) throws Exception {
        CapMetroConsumer capMetroConsumer = new CapMetroConsumer();
        capMetroConsumer.start();
        int numRestarts = 0;
        while (numRestarts < MAXIMUM_RESTARTS) {
            try {
                Thread.sleep(MONITOR_INTERVAL_MILLIS);
            } catch (InterruptedException ie) {
                logger.error("Main thread execution interrupted. Exiting application...", ie);
                System.exit(1);
            }
            if (capMetroConsumer.didAllTasksFail()) {
                capMetroConsumer = new CapMetroConsumer();
                capMetroConsumer.start();
                numRestarts++;
            }
        }
        logger.error("Maximum number of application restarts, " + MAXIMUM_RESTARTS + ", exceeded. Shutting down JVM.");
        System.exit(1);
    }
}
