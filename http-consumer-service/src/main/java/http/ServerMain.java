package http;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Jun. 04, 2017
 */
public class ServerMain {

    public static void main(String... args) {
        spark.Spark.get("/hello", (req, res) -> "Hello World");
    }
}
