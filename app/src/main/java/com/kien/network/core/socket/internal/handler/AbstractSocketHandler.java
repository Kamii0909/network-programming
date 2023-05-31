package com.kien.network.core.socket.internal.handler;

import com.kien.network.core.socket.api.adapter.SocketAdapterProvider;
import com.kien.network.core.socket.api.handler.SocketHandler;

/**
 * A skeleton implementation that use a SocketAdapter for user code
 */
public abstract non-sealed class AbstractSocketHandler<T, E extends SocketAdapterProvider<T>>
    implements SocketHandler<T> {
    protected final E adapterProvider;
    
    protected AbstractSocketHandler(E adapterProvider) {
        this.adapterProvider = adapterProvider;
    }
    
    public E getAdapterProvider() {
        return adapterProvider;
    }
}
