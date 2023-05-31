package com.kien.network.core.socket.api.handler;

import java.nio.channels.AsynchronousSocketChannel;

@FunctionalInterface
public non-sealed interface AsyncSocketHandler extends SocketHandler<AsynchronousSocketChannel> {
    /**
     * {@inheritDoc}
     * 
     * @param socket the accepted {@link AsynchronousSocketChannel} 
     */
    @Override
    void handle(AsynchronousSocketChannel socket);
}
