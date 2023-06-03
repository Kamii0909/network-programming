package com.kien.network.core.support;

import java.net.InetAddress;

import com.kien.network.core.WebClient;
import com.kien.network.core.socket.api.adapter.BlockingSocketAdapterProvider;
import com.kien.network.core.socket.internal.handler.blocking.ReuseThreadHandler;
import com.kien.network.core.socket.internal.handler.blocking.ThreadPerSocketHandler;
import com.kien.network.core.support.client.blocking.BlockingTCPClient;

public class WebClientFactory {
    private WebClientFactory() {
    }
    
    public static WebClient blocking(
        BlockingSocketAdapterProvider adapterProvider,
        InetAddress serverAddress,
        int serverPort,
        boolean reuse) {
        return reuse ? 
        new BlockingTCPClient(serverAddress, serverPort, new ReuseThreadHandler(adapterProvider)) :
            new BlockingTCPClient(serverAddress, serverPort, new ThreadPerSocketHandler(adapterProvider, 1));
    }
}
