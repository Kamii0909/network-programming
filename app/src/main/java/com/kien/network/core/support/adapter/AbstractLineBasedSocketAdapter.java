package com.kien.network.core.support.adapter;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.common.base.Splitter;
import com.kien.network.core.DelimiterConstants;
import com.kien.network.core.socket.api.adapter.SocketAdapter;
import com.kien.network.core.socket.api.context.SocketContext;

public abstract non-sealed class AbstractLineBasedSocketAdapter<T> implements SocketAdapter<T> {
    private static final String LINE_SEPARATOR = DelimiterConstants.DELIMITER;
    private static final Splitter splitter = Splitter.on(DelimiterConstants.DELIMITER);
    private SocketContext<T> context;
    private Queue<String> lines = new LinkedList<>();
    private String remaining = "";
    
    @Override
    public final void onBound(SocketContext<T> context) {
        @SuppressWarnings("unchecked") // context is a SocketContext<T> as we received from parameters.
        Class<? extends SocketContext<T>> contextClazz = (Class<? extends SocketContext<T>>) context.getClass();
        if (supportContext(contextClazz)) {
            this.context = context;
        }
        // Context is ready, we are ready to handle IO
        onReady();
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * This implementation do nothing.
     */
    @Override
    public void onUnbound(SocketContext<T> context) {
        // Do nothing by default
    }
    
    @Override
    public void onInactive(SocketContext<T> context) {
        onFinished();
    }
    
    protected final void parseData(byte[] data) {
        // Deal with fragmentation, subclass will be notified only when a complete line
        // is read. If the peer sent "lorem ipsum/r/n", there is a chance the first read
        // will only have "lorem" and the second one is "ipsum/r/n" or even more
        // fragmentation.
        String newData = remaining.concat(new String(data, UTF_8));
        List<String> newLines = splitter.splitToList(newData);
        // We would get the last unseparated text
        // For example: (input -> output -> remaining)
        // "lorem/r/nipsum/r/n" -> ["lorem", "ipsum", ""] -> ""
        // "lorem ip/r" -> ["lorem ipsum/r"] -> "lorem ipsum/r"
        // "lorem/r/n unfinis" -> ["lorem", " unfinis"] -> " unfinis"
        remaining = newLines.get(newLines.size() - 1);
        // Ignore the last element
        for (int i = 0; i < newLines.size() - 1; i++) {
            lines.add(newLines.get(i));
        }
        String s;
        while ((s = lines.poll()) != null) {
            newLineRead(s);
        }
    }
    
    /**
     * Check if the socketContextClazz is supported by subclass.
     * 
     * @param socketContextClazz
     * @return true if subclass can works with this context class, false otherwise
     */
    protected abstract boolean supportContext(Class<? extends SocketContext<T>> socketContextClazz);
    
    /**
     * Called when the connection is ready (before any IO happen).
     * <p>
     * The default implementation does nothing.
     */
    protected void onReady() {
        // Do nothing
    }
    
    /**
     * Called when the connection is going to be closed from this side.
     * <p>
     * The default implementation does nothing.
     */
    protected void onClosed() {
        // Do nothing
    }
    
    /**
     * Called when socket is closed by us or peer.
     * <p>
     * The default implementation does nothing.
     */
    protected void onFinished() {
        // Do nothing
    }
    
    /**
     * Initiate a non blocking read, returns immediately if nothing is read.
     * Otherwise, {@link #newLineRead(String)} is called.
     */
    protected final void readAvailableLines() {
        context.read();
    }
    
    /**
     * Callback invoked whenever a new line is read from the socket.
     * 
     * @param newLine the new line
     */
    protected abstract void newLineRead(String newLine);
    
    protected final void sendLine(String line) {
        context.write(line.getBytes(UTF_8));
        context.write(LINE_SEPARATOR.getBytes(UTF_8));
        context.flush();
    }
    
    protected final void closeSocket() {
        onClosed();
        context.close();
    }
    
    Queue<String> getLines() {
        return lines;
    }
    
    SocketContext<T> getContext() {
        return context;
    }
    
}
