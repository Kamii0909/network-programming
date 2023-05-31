package com.kien.network.core.socket.api.adapter;

import java.nio.channels.SocketChannel;

public non-sealed interface SocketChannelAdapterProvider extends SocketAdapterProvider<SocketChannel> {
    @Override
    SocketChannelAdapter get(SocketChannel socket);
}
