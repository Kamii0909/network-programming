package edu.hust.it4060.homework.multiplexing.server;

import java.nio.channels.SocketChannel;

import com.kien.network.core.socket.api.adapter.SocketChannelAdapter;
import com.kien.network.core.socket.api.adapter.SocketChannelAdapterProvider;

public class ServerGroupChatSocketAdapterProvider implements SocketChannelAdapterProvider {
    public SocketChannelAdapter get(SocketChannel socket) {
        return new ServerGroupChatSocketAdapter();
    }
}
