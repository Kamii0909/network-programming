package edu.hust.it4060.homework.blocking.server;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;

import com.kien.network.core.socket.api.adapter.BlockingSocketAdapter;
import com.kien.network.core.socket.api.adapter.BlockingSocketAdapterProvider;

public class GreetAndLogSocketAdapterProvider implements BlockingSocketAdapterProvider {
    
    public GreetAndLogSocketAdapterProvider(Path greetingFilePath, Path logFilePath) throws IOException {
        GreetAndLogSocketAdapter.initialize(greetingFilePath, logFilePath);
    }
    
    @Override
    public BlockingSocketAdapter get(Socket socket) {
        return new GreetAndLogSocketAdapter();
    }
    
}
