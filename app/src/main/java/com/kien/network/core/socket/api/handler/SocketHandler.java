package com.kien.network.core.socket.api.handler;

import com.kien.network.core.socket.api.adapter.SocketAdapter;
import com.kien.network.core.socket.internal.handler.AbstractSocketHandler;

/**
 * A socket handler is responsible for the IO strategy and thread model (which
 * IO operation runs on which thread).
 * 
 * @param T the Socket class
 */
public sealed interface SocketHandler<T> permits BlockingSocketHandler, SocketChannelHandler, AsyncSocketHandler,
    AbstractSocketHandler<T, ? extends SocketAdapter<T>> {
    
    /**
     * Handle the processing of an accepted socket. It is implementation
     * responsibility to query data from the socket, trigger any write or close the
     * socket.
     * 
     * @param t the accepted socket
     */
    void handle(T socket);
}
