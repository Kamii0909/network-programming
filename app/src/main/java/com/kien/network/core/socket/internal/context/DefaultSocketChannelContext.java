package com.kien.network.core.socket.internal.context;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.kien.network.core.socket.api.adapter.SocketChannelAdapter;
import com.kien.network.core.socket.api.context.SocketChannelContext;

public final class DefaultSocketChannelContext extends AbstractSocketContext<SocketChannel, SocketChannelAdapter>
    implements SocketChannelContext {
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
    private final SelectionKey selectionKey;
    
    public DefaultSocketChannelContext(SocketChannel socket,
        SocketChannelAdapter socketAdapter,
        SelectionKey selectionKey) {
        super(socket, socketAdapter);
        if (socket.isBlocking()) {
            throw new IllegalStateException("Socket cannot be blocking");
        }
        this.selectionKey = selectionKey;
    }
    
    @Override
    public void flush() {
        writeBuffer.flip();
        try {
            // Write spin loop optimization, 16 is the default of Netty
            int i = 16;
            while (i-- > 0) {
                socket.write(writeBuffer);
                if (writeBuffer.remaining() == 0) {
                    // We have written everything
                    selectionKey.interestOps(SelectionKey.OP_READ);
                    break;
                }
            }
            if (writeBuffer.remaining() > 0) {
                // We will comeback to write this later, not now
                selectionKey.interestOps(SelectionKey.OP_WRITE);
            } else {
                writeBuffer.clear();
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public void read() {
        try {
            readBuffer.clear();
            int read = socket.read(readBuffer);
            if (read > 0) {
                socketAdapter.onRead(this, readBuffer.flip().asReadOnlyBuffer());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public void write(ByteBuffer data) {
        if (writeBuffer.remaining() >= data.remaining()) {
            writeBuffer.put(data);
        } else {
            // resize the write buffer, this should be rare.
            writeBuffer.flip();
            ByteBuffer newBuffer = ByteBuffer.allocate(writeBuffer.remaining() + data.remaining());
            newBuffer.put(writeBuffer).put(data);
            writeBuffer = newBuffer;
        }
        
    }
    
}
