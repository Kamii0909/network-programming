package edu.hust.it4060.homework.util;

import java.net.InetAddress;

import com.kien.network.core.socket.api.adapter.BlockingSocketAdapterProvider;
import com.kien.network.core.support.WebClientFactory;

class Client implements Runnable {
    private final InetAddress serverAddress;
    private final int serverPort;
    private final BlockingSocketAdapterProvider adapterProvider;
    
    public Client(InetAddress serverAddress,
        int serverPort,
        BlockingSocketAdapterProvider adapterProvider) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.adapterProvider = adapterProvider;
    }
    
    @Override
    public void run() {
        try (var client = WebClientFactory.blocking(
            adapterProvider, serverAddress, serverPort, true)) {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
