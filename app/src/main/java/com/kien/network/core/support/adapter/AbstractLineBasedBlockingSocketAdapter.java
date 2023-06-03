package com.kien.network.core.support.adapter;

import java.net.Socket;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kien.network.core.socket.api.adapter.BlockingSocketAdapter;
import com.kien.network.core.socket.api.context.BlockingSocketContext;
import com.kien.network.core.socket.api.context.SocketContext;

public abstract class AbstractLineBasedBlockingSocketAdapter extends AbstractLineBasedSocketAdapter<Socket>
    implements BlockingSocketAdapter {
    private static final Logger log = LoggerFactory.getLogger(AbstractLineBasedBlockingSocketAdapter.class);
    
    @Override
    public void onActive(SocketContext<Socket> context) {
        log.info("Connected with {}", context.getSocket().getRemoteSocketAddress());
    }
    
    @Override
    public void onInactive(SocketContext<Socket> context) {
        onFinished();
        log.info("Dropped connection with {}", context.getSocket().getRemoteSocketAddress());
    }
    
    @Override
    public final void onRead(BlockingSocketContext ctx, byte[] data) {
        parseData(data);
    }
    
    @Override
    public final void onUnbound(SocketContext<Socket> context) {
        // Do nothing
    }
    
    @Override
    protected boolean supportContext(Class<? extends SocketContext<Socket>> socketContextClazz) {
        return BlockingSocketContext.class.isAssignableFrom(socketContextClazz);
    }
    
    /**
     * Initiate a blocking read if can't immediately trigger
     * {@link #newLineRead(String)}. Either way when this method returns,
     * {@link #newLineRead(String)} will be immediately called.
     */
    protected final void waitForNewInput() {
        if (!getLines().isEmpty()) {
            return;
        }
        byte[] buffer = new byte[1024];
        ((BlockingSocketContext) getContext()).blockingRead(buffer);
    }
    
    protected final SocketAddress getRemoteSocketAddress() {
        return getContext().getSocket().getRemoteSocketAddress();
    }
    
}
