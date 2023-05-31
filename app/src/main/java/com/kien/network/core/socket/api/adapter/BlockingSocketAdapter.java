package com.kien.network.core.socket.api.adapter;

import java.net.Socket;

import com.kien.network.core.socket.api.context.BlockingSocketContext;
import com.kien.network.core.socket.api.context.SocketContext;

public non-sealed interface BlockingSocketAdapter extends SocketAdapter<Socket> {
    /**
     * This will be called once a read operation is completed.
     */
    void onRead(BlockingSocketContext ctx, byte[] data);
    
    @Override
    default void onRead(SocketContext<Socket> context, Object data) {
        if (context instanceof BlockingSocketContext bsc && data instanceof byte[] ba) {
            onRead(bsc, ba);
        } else {
            throw new IllegalStateException("""
                Implementation does not match.
                SocketContext implementation is %s, expect BlockingSocketContext.
                data is a %s, expect byte[].
                """.formatted(context.getClass(), data.getClass()));
        }
    }
}
