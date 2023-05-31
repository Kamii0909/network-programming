package com.kien.network.core.socket.api.adapter;

import com.kien.network.core.socket.api.context.SocketContext;

/**
 * A Socket Adapter is the API between IO and bussiness logic, independant of
 * the underlying transport layer. Users are required to provide an
 * implementation of this interface (used through )
 */
public sealed interface SocketAdapter<T> permits BlockingSocketAdapter, SocketChannelAdapter {
    /**
     * Callback run after the underlying transport is active (ready). This will be
     * call right after the SocketContext is ready, IO operation may not be
     * available.
     */
    void onActive(SocketContext<T> context);
    
    /**
     * Callback run after the underlying transport is inactive (closed). IO
     * operation will be unavailable will throw exceptions.
     */
    void onInactive(SocketContext<T> context);
    
    /**
     * Called after a read IO operation is completed and returns new data.
     * 
     * @param data typically a byte[] or ByteBuffer
     */
    void onRead(SocketContext<T> context, Object data);
    
    /**
     * Callback run right after picked up by an IO thread (after being accepted).
     * This method is guaranteed to be ran every time a thread attempt an IO
     * operation on the socket without prerequisite knowledge about that socket. In
     * most scenario, it is likely only the first one is used. In async context,
     * this method can be called multiple times in multiple threads.
     */
    void onBound(SocketContext<T> context);
    
    /**
     * Callback run after a thread declare further IO operation is not guaranteed to
     * be ran in that thread.
     */
    void onUnbound(SocketContext<T> context);
}
