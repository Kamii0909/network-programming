package com.kien.network.core.support.server.blocking;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kien.network.core.socket.api.handler.BlockingSocketHandler;
import com.kien.network.core.support.AbstractWebServer;

public class BlockingTCPServer extends AbstractWebServer {
    private static final Logger logger = LoggerFactory.getLogger(BlockingTCPServer.class);
    private final BlockingSocketHandler socketHandler;
    private ServerSocket serverSocket;
    
    public BlockingTCPServer(InetAddress inetAddress,
        int port,
        BlockingSocketHandler socketHandler,
        ExecutorService executorService) {
        super(inetAddress, port);
        this.socketHandler = socketHandler;
    }
    
    @SuppressWarnings("preview")
    public BlockingTCPServer(InetAddress inetAddress, int port, BlockingSocketHandler socketHandler) {
        this(inetAddress, port, socketHandler, Executors.newVirtualThreadPerTaskExecutor());
    }
    
    public BlockingTCPServer(BlockingSocketHandler socketHandler) {
        this(InetAddress.getLoopbackAddress(), 8080, socketHandler);
    }
    
    @Override
    protected void startListenSocket() {
        try {
            serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, 10, inetAddress);
        } catch (IOException e) {
            logger.error("Server Socket could not be started", e);
        }
    }
    
    @Override
    protected void handleRequests() {
        try {
            Socket socket = serverSocket.accept();
            socketHandler.handle(socket);
        } catch (IOException e) {
            logger.error("Exception in accepting new connections", e);
        }
        
    }
    
    @Override
    protected void cleanup() {
        try {
            if (socketHandler instanceof Closeable cs) {
                cs.close();
            }
            serverSocket.close();
        } catch (IOException e) {
            logger.error("Failed to gracefully shutdown ServerSocket {}", serverSocket, e);
        }
    }
    
}
