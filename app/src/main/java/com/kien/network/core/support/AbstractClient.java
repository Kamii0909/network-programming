package com.kien.network.core.support;

import java.net.InetAddress;

import com.kien.network.core.WebClient;

public abstract class AbstractClient implements WebClient {
    protected final InetAddress serverAddress;
    protected final int serverPort;

    protected AbstractClient(InetAddress serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
}
