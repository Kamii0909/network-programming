package com.kien.network.core.support.server.multiplexing;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kien.network.core.socket.api.handler.SocketChannelHandler;
import com.kien.network.core.support.AbstractWebServer;

public class PollingTCPServer extends AbstractWebServer {
    
    private static final Logger log = LoggerFactory.getLogger(PollingTCPServer.class);
    private ServerSocketChannel serverSocket;
    private final SocketChannelHandler socketHandler;
    
    public PollingTCPServer(InetAddress inetAddress,
        int port,
        SocketChannelHandler socketHandler) {
        super(inetAddress, port);
        this.socketHandler = socketHandler;
    }
    
    @Override
    protected void startListenSocket() throws IOException {
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(inetAddress, port));
    }
    
    @Override
    protected void handleRequests() {
        // We will accept new socket in a blocking fashion and delegate the processing
        // to the handler
        while (true) {
            try {
                SocketChannel socketChannel = serverSocket.accept();
                socketChannel.configureBlocking(false);
                socketHandler.handle(socketChannel);
            } catch (IOException e) {
                log.warn("Unable to accept new connection", e);
            }
        }
    }
    
    @Override
    protected void cleanup() throws IOException {
        try {
            if (socketHandler instanceof Closeable ac) {
                ac.close();
            }
            serverSocket.close();
        } catch (Exception e) {
            log.error("Unable to gracefully shutdown PollingTCPServer {}", serverSocket, e);
        }
    }
    
}
