package http;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Jun. 04, 2017
 */

import org.junit.Test;

import static spark.Spark.get;

public class ServerSpec {

    @Test
    public void sparkTest() throws Exception {
        get("/hello", (req, res) -> "Hello World");
        Thread.sleep(30000);
    }

}
