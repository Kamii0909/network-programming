package com.kien.network.core.socket.internal.context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import com.kien.network.core.socket.api.adapter.BlockingSocketAdapter;
import com.kien.network.core.socket.api.context.BlockingSocketContext;

public final class DefaultBlockingSocketContext extends AbstractSocketContext<Socket, BlockingSocketAdapter>
    implements BlockingSocketContext {
    private final BufferedOutputStream socketOutputStream;
    private final BufferedInputStream socketInputStream;
    
    public DefaultBlockingSocketContext(Socket socket, BlockingSocketAdapter socketAdapter) throws IOException {
        super(socket, socketAdapter);
        this.socketOutputStream = new BufferedOutputStream(socket.getOutputStream());
        this.socketInputStream = new BufferedInputStream(socket.getInputStream());
    }
    
    @Override
    public void flush() {
        try {
            socketOutputStream.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public void read() {
        try {
            int i;
            if ((i = socketInputStream.available()) != 0) {
                byte[] data = new byte[i];
                int read = socketInputStream.read(data);
                socketAdapter.onRead(this, Arrays.copyOf(data, read));
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public int blockingRead(byte[] buffer) {
        try {
            int read = socketInputStream.read(buffer);
            if (read > 0) {
                socketAdapter.onRead(this, Arrays.copyOf(buffer, read));
            }
            return read;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public void close() {
        try {
            socketInputStream.close();
            socketOutputStream.close();
            socket.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public void write(byte[] data) {
        try {
            socketOutputStream.write(data);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
}
