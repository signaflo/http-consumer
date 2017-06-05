package http;

import com.sun.net.httpserver.*;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Jun. 04, 2017
 */
public class Server {

    private final HttpServer httpsServer;

    public Server(String hostName, int port) throws Exception {
        InetSocketAddress address = new InetSocketAddress(hostName, port);
        this.httpsServer = HttpServer.create(address, 100);
        //this.httpsServer.setHttpsConfigurator(new HttpsConfigurator(SSLContext.getDefault()));
    }



}
