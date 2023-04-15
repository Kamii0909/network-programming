package edu.hust.it4060.common.client;

import java.net.InetAddress;

public abstract class AbstractClient implements TCPClient {
    protected final InetAddress serverAddress;
    protected final int serverPort;

    protected AbstractClient(InetAddress serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
}
