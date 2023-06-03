package edu.hust.it4060.homework.blocking.server;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;

import com.kien.network.core.socket.api.adapter.BlockingSocketAdapter;
import com.kien.network.core.socket.api.adapter.BlockingSocketAdapterProvider;

public class GetStuIn4SocketAdapterProvider implements BlockingSocketAdapterProvider {
    
    public GetStuIn4SocketAdapterProvider(Path logFilePath) throws IOException {
        GetStuIn4SocketAdapter.initialize(logFilePath);
    }
    @Override
    public BlockingSocketAdapter get(Socket socket) {
        return new GetStuIn4SocketAdapter();
    }
    
}
