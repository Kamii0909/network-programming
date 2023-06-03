package edu.hust.it4060.homework.week3;

import java.net.InetAddress;

import com.kien.network.core.support.WebServerFactory;

import edu.hust.it4060.homework.multiplexing.server.ServerGroupChatSocketAdapterProvider;

class NoClientChatServerMain {
    public static void main(String[] args) throws InterruptedException {
        var server = WebServerFactory.multiplex(new ServerGroupChatSocketAdapterProvider(),
            InetAddress.getLoopbackAddress(), 8080);
        server.addShutdownHook();
        server.run().join();
    }
}
