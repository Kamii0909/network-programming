package com.kien.network.core.socket.internal.handler.multiplex;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kien.network.core.socket.api.adapter.SocketChannelAdapterProvider;
import com.kien.network.core.socket.api.handler.SocketChannelHandler;
import com.kien.network.core.socket.internal.handler.AbstractSocketHandler;

/**
 * A handler that use multiple (possibly 1) Selectors to multiplex IO.
 */
public final class MultiSelectorHandler extends AbstractSocketHandler<SocketChannel, SocketChannelAdapterProvider>
    implements SocketChannelHandler, AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(MultiSelectorHandler.class);
    private final ThreadBoundSelector[] selectorPool;
    // Assign sockets to selectors in a round robin fashion
    private int counter;
    
    public MultiSelectorHandler(SocketChannelAdapterProvider adapterProvider, int nSelectors) {
        super(adapterProvider);
        this.selectorPool = new ThreadBoundSelector[nSelectors];
        for (int i = 0; i < nSelectors; i++) {
            try {
                selectorPool[i] = new ThreadBoundSelector(
                    "Selector-IO-Thread-" + i,
                    SelectorProvider.provider(),
                    adapterProvider);
                
                selectorPool[i].start();
            } catch (IOException e) {
                log.warn("Unable to create new selector", e);
            }
        }
    }
    
    @Override
    public void handle(SocketChannel socket) {
        if (!selectorPool[counter].register(socket)) {
            // everything is shutting down, just close the new socket
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore
                log.error("Error closing connection when shutting down", e);
            }
        } else if (counter + 1 >= selectorPool.length) {
            counter = 0;
        } else {
            counter += 1;
        }
    }
    
    @Override
    public void close() throws Exception {
        for (ThreadBoundSelector selector : selectorPool) {
            // We will left handling connection as it is and close pending connection
            selector.shutdown(null, pc -> {
                try {
                    pc.close();
                } catch (IOException e) {
                    try {
                        log.warn("Unable to close socket channel with {}", pc.getRemoteAddress());
                    } catch (IOException e1) {
                        // Won't happen
                        log.warn("Unable to find remote address of socket channel {}", pc);
                    }
                }
                return null;
            });
        }
    }
    
}
