package com.kien.network.core.support;

import java.net.InetAddress;

import com.kien.network.core.WebServer;
import com.kien.network.core.socket.api.adapter.BlockingSocketAdapterProvider;
import com.kien.network.core.socket.api.adapter.SocketChannelAdapterProvider;
import com.kien.network.core.socket.internal.handler.blocking.ThreadPerSocketHandler;
import com.kien.network.core.socket.internal.handler.multiplex.MultiSelectorHandler;
import com.kien.network.core.support.server.blocking.BlockingTCPServer;
import com.kien.network.core.support.server.multiplexing.PollingTCPServer;

/**
 * The primary way to create TCP Server
 */
public class WebServerFactory {
    private WebServerFactory() {
    }
    
    public static WebServer blocking(
        BlockingSocketAdapterProvider adapterProvider,
        InetAddress inetAddress,
        int port,
        int nThread) {
        return new BlockingTCPServer(inetAddress, port, new ThreadPerSocketHandler(adapterProvider, nThread));
    }
    
    public static WebServer blocking(
        BlockingSocketAdapterProvider adapterProvider,
        InetAddress inetAddress,
        int port) {
        return new BlockingTCPServer(inetAddress, port, new ThreadPerSocketHandler(adapterProvider));
    }
    
    public static WebServer multiplex(
        SocketChannelAdapterProvider adapterProvider,
        InetAddress inetAddress,
        int port,
        int nSelectors) {
        return new PollingTCPServer(inetAddress, port, new MultiSelectorHandler(adapterProvider, nSelectors));
    }
    
    public static WebServer multiplex(
        SocketChannelAdapterProvider adapterProvider,
        InetAddress inetAddress,
        int port) {
        return new PollingTCPServer(inetAddress, port, new MultiSelectorHandler(adapterProvider, 1));
    }
}
