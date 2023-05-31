package com.kien.network.core.socket.internal.handler.blocking;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kien.network.core.socket.api.adapter.BlockingSocketAdapter;
import com.kien.network.core.socket.api.adapter.BlockingSocketAdapterProvider;
import com.kien.network.core.socket.api.context.BlockingSocketContext;
import com.kien.network.core.socket.api.handler.BlockingSocketHandler;
import com.kien.network.core.socket.internal.context.DefaultBlockingSocketContext;
import com.kien.network.core.socket.internal.handler.AbstractSocketHandler;

/**
 * Each socket will be bound into a thread (virtual or platform). The handle
 * method in this class will not block as it use an internal thread pool.
 */
public final class ThreadPerSocketHandler extends AbstractSocketHandler<Socket, BlockingSocketAdapterProvider>
    implements BlockingSocketHandler, AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(ThreadPerSocketHandler.class);
    private final ExecutorService executorService;
    
    public ThreadPerSocketHandler(BlockingSocketAdapterProvider adapterProvider,
        ExecutorService executorService) {
        super(adapterProvider);
        this.executorService = executorService;
    }
    
    /**
     * Create a SocketHandler that use a thread pool and attach each Socket to a
     * thread.
     */
    public ThreadPerSocketHandler(BlockingSocketAdapterProvider adapterProvider, int nThreads) {
        this(adapterProvider, Executors.newFixedThreadPool(nThreads));
    }
    
    /**
     * Create a SocketHandler that create a new Virtual Thread for each Socket.
     */
    @SuppressWarnings("preview")
    public ThreadPerSocketHandler(BlockingSocketAdapterProvider adapterProvider) {
        this(adapterProvider, Executors.newVirtualThreadPerTaskExecutor());
    }
    
    @Override
    public void handle(Socket socket) {
        try {
            BlockingSocketAdapter adapter = adapterProvider.get(socket);
            BlockingSocketContext context = new DefaultBlockingSocketContext(socket, adapter);
            
            adapter.onActive(context);
            
            executorService.submit(new ThreadBoundSocketTask(context));
        } catch (IOException e) {
            // Shouldn't happen
            log.error("Unexpected IOException when creating input and output stream for Socket {}", socket, e);
        }
    }
    
    private static class ThreadBoundSocketTask implements Runnable {
        BlockingSocketContext context;
        
        public ThreadBoundSocketTask(BlockingSocketContext context) {
            this.context = context;
        }
        
        @Override
        public void run() {
            BlockingSocketAdapter adapter = context.getSocketAdapter();
            Socket socket = context.getSocket();
            adapter.onBound(context);
            
            try (BufferedInputStream inputStream =
                new BufferedInputStream(socket.getInputStream())) {
                
                byte[] data = new byte[1024];
                int read;
                while ((read = inputStream.read(data)) != -1) {
                    adapter.onRead(context, Arrays.copyOf(data, read));
                }
            } catch (IOException e) {
                log.warn("Exception within server", e);
            }
            
            adapter.onInactive(context);
        }
        
    }

    @Override
    public void close() throws Exception {
        executorService.shutdown();
    }
}
