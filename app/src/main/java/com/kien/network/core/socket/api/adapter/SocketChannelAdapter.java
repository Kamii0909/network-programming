package com.kien.network.core.socket.api.adapter;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.kien.network.core.socket.api.context.SocketChannelContext;
import com.kien.network.core.socket.api.context.SocketContext;

public non-sealed interface SocketChannelAdapter extends SocketAdapter<SocketChannel> {
    /**
     * Called when a socket read IO is completed. The buffer is only valid for the
     * duration of the method call. If data should be retained after the method
     * returns, copy the content of the buffer. This method will be ran on the IO
     * thread, make sure to not block anything. If there is any long running
     * operation, use another thread, save the {@code ctx} there.
     * 
     * @param data a ReadOnly ByteBuffer
     */
    void onRead(SocketChannelContext ctx, ByteBuffer data);
    
    @Override
    default void onRead(SocketContext<SocketChannel> context, Object data) {
        if (context instanceof SocketChannelContext scc && data instanceof ByteBuffer bf) {
            onRead(scc, bf);
        } else {
            throw new IllegalStateException("""
                Implementation does not match.
                SocketContext implementation is %s, expect SocketChannelContext.
                data is a %s, expect ByteBuffer.
                """.formatted(context.getClass(), data.getClass()));
        }
    }
}
