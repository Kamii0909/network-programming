package edu.hust.it4060.homework.week3;

import java.net.InetAddress;

import com.kien.network.core.WebServer;
import com.kien.network.core.support.WebClientFactory;
import com.kien.network.core.support.WebServerFactory;

import edu.hust.it4060.homework.blocking.client.ClientGroupChatSocketAdapterProvider;
import edu.hust.it4060.homework.multiplexing.server.ServerGroupChatSocketAdapterProvider;

class ChatServerMain {
    public static void main(String[] args) throws InterruptedException {
        WebServer server = WebServerFactory
            .multiplex(new ServerGroupChatSocketAdapterProvider(), InetAddress.getLoopbackAddress(), 8080);
        server.addShutdownHook();
        Thread bossThread = server.run();
        
        new Thread(() -> WebClientFactory.blocking(new ClientGroupChatSocketAdapterProvider(),
            InetAddress.getLoopbackAddress(), 8080, true).connect()).start();
        new Thread(() -> WebClientFactory.blocking(new ClientGroupChatSocketAdapterProvider(),
            InetAddress.getLoopbackAddress(), 8080, true).connect()).start();
        bossThread.join();
    }
}
