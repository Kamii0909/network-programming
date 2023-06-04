package edu.hust.it4060.homework.week5;

import java.net.InetAddress;

import com.kien.network.core.WebServer;
import com.kien.network.core.support.WebServerFactory;

import edu.hust.it4060.homework.blocking.server.HelloWorldHttpSocketAdapterProvider;

public class HttpServerMain {
    public static void main(String[] args) throws InterruptedException {
        WebServer server = WebServerFactory.blocking(
            new HelloWorldHttpSocketAdapterProvider(),
            InetAddress.getLoopbackAddress(), 8080);
        server.addShutdownHook();
        server.run().join();
    }
}
