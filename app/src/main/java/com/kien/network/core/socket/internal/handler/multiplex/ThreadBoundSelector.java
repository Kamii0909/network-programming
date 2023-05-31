package com.kien.network.core.socket.internal.handler.multiplex;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kien.network.core.socket.api.adapter.SocketChannelAdapter;
import com.kien.network.core.socket.api.adapter.SocketChannelAdapterProvider;
import com.kien.network.core.socket.internal.context.DefaultSocketChannelContext;

/**
 * A Thread that use a Selector to multiplex IO.
 */
public class ThreadBoundSelector extends Thread {
    private static final Logger log = LoggerFactory.getLogger(ThreadBoundSelector.class);
    private final Selector selector;
    private final SocketChannelAdapterProvider adapterProvider;
    private final ConcurrentLinkedQueue<SocketChannel> pendingConnection = new ConcurrentLinkedQueue<>();
    private boolean shuttingDown = false;
    private Consumer<SelectionKey> activeConnectionConsumerWhenShuttingDown;
    private UnaryOperator<SocketChannel> pendingConnectionOperatorWhenShuttingDown;
    
    /**
     * Create a new unstarted Thread that bound a selector
     * 
     * @throws IOException if open a new selector fails
     */
    public ThreadBoundSelector(
        String threadName,
        SelectorProvider provider,
        SocketChannelAdapterProvider adapterProvider) throws IOException {
        super(threadName);
        this.selector = provider.openSelector();
        this.adapterProvider = adapterProvider;
    }
    
    /**
     * Whether this worker thread is not shutting down.
     */
    public boolean isActive() {
        return shuttingDown;
    }
    
    /**
     * Register a SocketChannel with the selector in this thread. The registration
     * is not guaranteed to happen immediately (or even at all).
     * 
     * @param channel the new accepted channel
     * @return true if the registration was sucessful, false otherwise (the selector
     *         is shutting down)
     */
    public boolean register(SocketChannel channel) {
        if (shuttingDown) {
            return false;
        }
        pendingConnection.offer(channel);
        selector.wakeup();
        return true;
    }
    
    /**
     * Gracefully stop the selector thread, refuse any new connection. Calling this
     * method twice have no effect but replacing the arguments. The argument
     * invocations are best-effort only.
     * 
     * @param onActive  action to be performed on active registered connections. The
     *                  SelectionKey is valid for the duration of the consumer call.
     *                  After it returns, using the provided SelectionKey may
     *                  introduce undefined behavior.
     * @param onPending action to be performed on pending connection. Return null
     *                  from the Function will not register the pending connection
     *                  with this selector.
     * @apiNote To deregister the SelectionKey (for example transferring the
     *          SocketChannel to another Selector), cancel the SelectionKey in
     *          {@code activeConAction}.
     */
    public void shutdown(Consumer<SelectionKey> onActive, UnaryOperator<SocketChannel> onPending) {
        if (onActive != null) {
            this.activeConnectionConsumerWhenShuttingDown = onActive;
        }
        if (onPending != null) {
            this.pendingConnectionOperatorWhenShuttingDown = onPending;
        }
        shuttingDown = true;
    }
    
    @Override
    public void run() {
        while (true) {
            if (Thread.interrupted()) {
                shuttingDown = true;
            }
            doSelect();
        }
    }
    
    private void doSelect() {
        if (shuttingDown) {
            // Avoid consuming the selection key with different policy if
            // activeConnectionConsumerWhenShuttingDown is changed when processing.
            Consumer<SelectionKey> consumerSnapshot = activeConnectionConsumerWhenShuttingDown;
            if (consumerSnapshot != null) {
                selector.keys().forEach(consumerSnapshot);
            }
        }
        try {
            int i = selector.select();
            SocketChannel channel;
            while ((channel = pendingConnection.poll()) != null) {
                doRegister(channel);
            }
            if (i > 0) {
                doHandle();
            }
            
        } catch (IOException e) {
            log.warn("Exception when selecting in thread {}", getName());
        }
        
    }
    
    private void doHandle() {
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            if (key.isReadable()) {
                DefaultSocketChannelContext context = (DefaultSocketChannelContext) key.attachment();
                context.read();
            } 
            if (key.isWritable()) {
                // There is a pending write to be completed
                DefaultSocketChannelContext context = (DefaultSocketChannelContext) key.attachment();
                context.flush();
            } 
            iterator.remove();
        }
    }
    
    /**
     * Register with the selector in this thread a SocketChannel.
     * <p>
     * The attachment will be the DefaultSocketChannelContext.
     * 
     * @implNote This method will only run on the selector thread.
     */
    private void doRegister(SocketChannel channel) {
        if (channel == null) {
            // No more new connections
            return;
        }
        
        // We give a chance for a pending connection operator policy change to take
        // effect every time we try to add new connection
        if (shuttingDown && pendingConnectionOperatorWhenShuttingDown != null) {
            channel = pendingConnectionOperatorWhenShuttingDown.apply(channel);
            if (channel == null) {
                return;
            }
        }
        
        try {
            SocketChannelAdapter adapter = adapterProvider.get(channel);
            
            SelectionKey sk = channel.register(selector, SelectionKey.OP_READ);
            DefaultSocketChannelContext context = new DefaultSocketChannelContext(channel, adapter, sk);
            sk.attach(context);
            adapter.onActive(context);
            adapter.onBound(context);
        } catch (ClosedChannelException e) {
            // Probably won't happen
            try {
                log.warn("Channel with {} is already closed", channel.getRemoteAddress());
            } catch (IOException e1) {
                // Won't happen
                log.warn("Unable to locate channel remote address from thread {}", getName(), e);
            }
        }
        
    }
}
