package com.kien.network.core.socket.api.context;

import java.net.Socket;
import java.nio.ByteBuffer;

import com.kien.network.core.socket.api.adapter.BlockingSocketAdapter;
import com.kien.network.core.socket.internal.context.DefaultBlockingSocketContext;

public sealed interface BlockingSocketContext extends SocketContext<Socket> permits DefaultBlockingSocketContext {
    /**
     * Write to the underlying transport layer. This method will block.
     */
    void write(byte[] data);
    
    @Override
    default void write(Object data) {
        if (data instanceof byte[] ba) {
            write(ba);
        } else if (data instanceof ByteBuffer bf) {
            byte[] ba = new byte[bf.remaining()];
            bf.get(ba);
            write(ba);
        } else {
            throw new IllegalArgumentException("BlockingSocketContext only accept byte[] and ByteBuffer");
        }
    }

    @Override
    BlockingSocketAdapter getSocketAdapter();
        
    
}
