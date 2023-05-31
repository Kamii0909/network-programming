package com.kien.network.core.socket.api.context;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.kien.network.core.socket.internal.context.DefaultSocketChannelContext;

public sealed interface SocketChannelContext extends SocketContext<SocketChannel> permits DefaultSocketChannelContext {
    /**
     * Write to an internal buffer. Remember to call {@link #flush()}.
     * 
     * @param data data
     */
    void write(ByteBuffer data);
    
    /**
     * {@inheritDoc}
     * 
     * @implNote This method will not block
     */
    @Override
    void flush();
    
    @Override
    default void write(Object data) {
        if (data instanceof ByteBuffer bf) {
            write(bf);
        } else if (data instanceof byte[] ba) {
            write(ByteBuffer.wrap(ba));
        }
    }
}
