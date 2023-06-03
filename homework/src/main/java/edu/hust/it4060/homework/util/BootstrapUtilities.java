package edu.hust.it4060.homework.util;

import java.net.InetAddress;

import com.kien.network.core.socket.api.adapter.BlockingSocketAdapterProvider;

public final class BootstrapUtilities {
    
    private BootstrapUtilities() {
    }
    
    public static void runBlockingClient(boolean reuseThread,
        InetAddress serverAddress,
        int serverPort,
        BlockingSocketAdapterProvider adapterProvider) {

        if (reuseThread) {
            new Client(serverAddress, serverPort, adapterProvider).run();
        } else {
            new Thread(new Client(serverAddress, serverPort, adapterProvider)).start();
        }
    }
}
