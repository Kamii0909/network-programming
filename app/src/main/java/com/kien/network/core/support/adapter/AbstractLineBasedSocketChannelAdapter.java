package com.kien.network.core.support.adapter;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kien.network.core.socket.api.adapter.SocketChannelAdapter;
import com.kien.network.core.socket.api.context.SocketChannelContext;
import com.kien.network.core.socket.api.context.SocketContext;
import com.kien.network.core.support.ExceptionUtils;

public abstract class AbstractLineBasedSocketChannelAdapter extends AbstractLineBasedSocketAdapter<SocketChannel>
    implements SocketChannelAdapter {
    private static final Logger log = LoggerFactory.getLogger(AbstractLineBasedSocketChannelAdapter.class);
    
    @Override
    public final void onActive(SocketContext<SocketChannel> context) {
        ExceptionUtils.unlikely(
            () -> log.info("Connected to {}", context.getSocket().getRemoteAddress()),
            e -> log.error("Internal error", e));
    }
    
    @Override
    protected final boolean supportContext(Class<? extends SocketContext<SocketChannel>> socketContextClazz) {
        return SocketChannelContext.class.isAssignableFrom(socketContextClazz);
    }
    
    @Override
    public final void onUnbound(SocketContext<SocketChannel> context) {
        // Do nothing
    }
    
    @Override
    public final void onInactive(SocketContext<SocketChannel> context) {
        super.onInactive(context);
        ExceptionUtils.unlikely(
            () -> log.info("Dropped connection with {}", context.getSocket().getRemoteAddress()),
            e -> log.error("Internal server error", e));
    }
    
    @Override
    public final void onRead(SocketChannelContext ctx, ByteBuffer buffer) {
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        parseData(data);
    }
    
    protected final SocketAddress getRemoteSocketAddress() {
        return ExceptionUtils.unlikely(() -> getContext().getSocket().getRemoteAddress());
    }
}
