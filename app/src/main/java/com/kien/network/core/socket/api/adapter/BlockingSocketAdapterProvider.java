package com.kien.network.core.socket.api.adapter;

import java.net.Socket;

/**
 * Required implementation to use with BlockingWebServer.
 */
@FunctionalInterface
public non-sealed interface BlockingSocketAdapterProvider extends SocketAdapterProvider<Socket> {
    /**
     * Get the {@link BlockingSocketAdapter} that will be bound to a specific
     * socket. Different socket can use different {@link BlockingSocketAdapter}
     * instances, reuse the adapter for Socket with the same Remote IP Address, or
     * return a singleton.
     */
    @Override
    BlockingSocketAdapter get(Socket socket);
}
