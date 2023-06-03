package com.kien.network.core.socket.internal.handler.blocking;

import java.io.IOException;
import java.net.Socket;

import com.kien.network.core.socket.api.adapter.BlockingSocketAdapter;
import com.kien.network.core.socket.api.adapter.BlockingSocketAdapterProvider;
import com.kien.network.core.socket.api.context.BlockingSocketContext;
import com.kien.network.core.socket.api.handler.BlockingSocketHandler;
import com.kien.network.core.socket.internal.context.DefaultBlockingSocketContext;
import com.kien.network.core.socket.internal.handler.AbstractSocketHandler;

/**
 * Handler that reuse the calling thread for blocking IO.
 */
public class ReuseThreadHandler extends AbstractSocketHandler<Socket, BlockingSocketAdapterProvider>
    implements BlockingSocketHandler {
    
    public ReuseThreadHandler(BlockingSocketAdapterProvider adapterProvider) {
        super(adapterProvider);
    }
    
    @Override
    public void handle(Socket socket) {
        try {
            BlockingSocketAdapter adapter = adapterProvider.get(socket);
            BlockingSocketContext context = new DefaultBlockingSocketContext(socket, adapter);
            
            adapter.onActive(context);
            adapter.onBound(context);
            if (!socket.isClosed()) {
                byte[] buffer = new byte[1024];
                do {
                    // No op, context will call onRead
                } while (context.blockingRead(buffer) != -1);
            }
            
            adapter.onInactive(context);
        } catch (IOException e) {
            // Shouldn't happen
            e.printStackTrace();
        }
    }
}
