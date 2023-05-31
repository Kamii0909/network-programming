package com.kien.network.core.support.adapter;

import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kien.network.core.socket.api.adapter.BlockingSocketAdapter;
import com.kien.network.core.socket.api.context.BlockingSocketContext;
import com.kien.network.core.socket.api.context.SocketContext;

public abstract class AbstractLineBasedSocketAdapter implements BlockingSocketAdapter {
    private static final Logger log = LoggerFactory.getLogger(AbstractLineBasedSocketAdapter.class);
    private final String endOfLineDelimiter;
    private SocketContext<Socket> context;
    
    protected AbstractLineBasedSocketAdapter(String endOfLineDelimiter) {
        this.endOfLineDelimiter = endOfLineDelimiter;
    }
    
    @Override
    public void onActive(SocketContext<Socket> context) {
        log.info("Picked up the connection with {} on thread {} with id {}",
            context.getSocket().getRemoteSocketAddress(),
            Thread.currentThread().getName(), Thread.currentThread().threadId());
    }
    
    @Override
    public void onInactive(SocketContext<Socket> context) {
        log.info("Dropped connection with {}", context.getSocket().getRemoteSocketAddress());
    }
    
    @Override
    public void onBound(SocketContext<Socket> context) {
        log.info("Started handling connection with {} from thread {} with id {}",
            context.getSocket().getRemoteSocketAddress(), Thread.currentThread().getName(),
            Thread.currentThread().threadId());
        // Everything is ready, we are ready to handle IO
        this.context = context;
        onReady();
    }
    
    /**
     * Called when the connection is ready (before any IO happen).
     */
    protected void onReady() {
        // Do nothing
    }

    /**
     * Called when the connection is going to be closed.
     */
    protected void onFinished() {
        // Do nothing
    }
    
    @Override
    public void onUnbound(SocketContext<Socket> context) {
        // Do nothing
    }
    
    @Override
    public void onRead(BlockingSocketContext ctx, byte[] data) {
        String[] lines = new String(data, StandardCharsets.UTF_8).split(endOfLineDelimiter);
        for (String line : lines) {
            newLineRead(line);
        }
    }
    
    protected abstract void newLineRead(String newLine);
    
    protected final void sendLine(String line) {
        context.write(line.getBytes(StandardCharsets.UTF_8));
        context.write(endOfLineDelimiter.getBytes(StandardCharsets.UTF_8));
        context.close();
    }
    
    protected final void closeSocket() {
        onFinished();
        context.close();
    }
    
    protected final SocketAddress getRemoteSocketAddress() {
        return context.getSocket().getRemoteSocketAddress();
    }
    
}
