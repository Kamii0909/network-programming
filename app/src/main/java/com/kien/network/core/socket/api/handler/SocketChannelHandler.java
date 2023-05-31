package com.kien.network.core.socket.api.handler;

import java.nio.channels.SocketChannel;

@FunctionalInterface
public non-sealed interface SocketChannelHandler extends SocketHandler<SocketChannel> {
    /**
     * {@inheritDoc}
     * <p>
     * The default implementation use its own thread pool. It is possible to simply
     * run this call in the boss thread.
     * 
     * @param channel the {@link java.nio.channels.SocketChannel} that got accepted
     */
    @Override
    void handle(SocketChannel channel);
}
