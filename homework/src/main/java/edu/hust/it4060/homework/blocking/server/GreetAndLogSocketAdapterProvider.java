package edu.hust.it4060.homework.blocking.server;

import java.net.Socket;

import com.kien.network.core.socket.api.adapter.BlockingSocketAdapter;
import com.kien.network.core.socket.api.adapter.BlockingSocketAdapterProvider;

public class GreetAndLogSocketAdapterProvider implements BlockingSocketAdapterProvider {
    
    @Override
    public BlockingSocketAdapter get(Socket socket) {
        return new GreetAndLogSocketAdapter();
    }
    
}
