package execution;

import java.io.File;

import static spark.Spark.get;
import static spark.Spark.port;

/**
 *
 * @author Jacob Rachiele
 *         Jun. 14, 2017
 */
public final class Application {

    private static final String slash = File.separator;

    private Application() {}

    public static void main(String... args) {

        ScheduledExecutorController controller = new ScheduledExecutorController();

        final int sparkPort = 4567;
        port(sparkPort);
        String path = slash + "app";
        get(path, controller.route());

    }
}
