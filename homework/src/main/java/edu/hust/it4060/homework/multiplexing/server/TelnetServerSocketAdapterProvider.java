package edu.hust.it4060.homework.multiplexing.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;

import com.kien.network.core.socket.api.adapter.SocketChannelAdapter;
import com.kien.network.core.socket.api.adapter.SocketChannelAdapterProvider;

public class TelnetServerSocketAdapterProvider implements SocketChannelAdapterProvider {
    
    public TelnetServerSocketAdapterProvider(Path userFilePath) throws IOException {
        TelnetServerSocketAdapter.initialize(userFilePath);
    }
    
    public SocketChannelAdapter get(SocketChannel socket) {
        return new TelnetServerSocketAdapter();
    };
}
