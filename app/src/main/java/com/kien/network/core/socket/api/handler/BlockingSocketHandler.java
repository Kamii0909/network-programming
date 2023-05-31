package com.kien.network.core.socket.api.handler;

import java.net.Socket;

@FunctionalInterface
public non-sealed interface BlockingSocketHandler extends SocketHandler<Socket> {
    /**
     * {@inheritDoc}
     * <p>
     * Handle the accepted {@link java.net.Socket}
     */
    @Override
    void handle(Socket socket);

}
