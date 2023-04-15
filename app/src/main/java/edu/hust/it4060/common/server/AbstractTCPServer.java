package edu.hust.it4060.common.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTCPServer implements TCPServer {
    private static final Logger log = LoggerFactory.getLogger(AbstractTCPServer.class);
    protected final InetAddress inetAddress;
    protected final int port;
    private Thread bossThread;
    private boolean running;
    
    protected AbstractTCPServer(InetSocketAddress socketAddress) {
        this.inetAddress = socketAddress.getAddress();
        this.port = socketAddress.getPort();
    }
    
    protected AbstractTCPServer(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }
    
    @Override
    @SuppressWarnings("preview")
    public Thread start() {
        bossThread = Thread
            .ofPlatform()
            .daemon()
            .name("Server Thread")
            .unstarted(this::startBossThread);
        
        synchronized (this) {
            bossThread.start();
            while (!running) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
        return bossThread;
    }
    
    @Override
    public void close() throws Exception {
        log.info("Stopping server...");

        synchronized (this) {
            this.running = false;
            if (bossThread != null) {
                bossThread.interrupt();
                cleanup();
                this.running = false;
            }
        }
        log.info("Server stopped!");

    }
    
    protected abstract void startListenSocket();
    
    protected abstract void acceptNewSocket();
    
    protected abstract void cleanup();
    
    private void startBossThread() {
        startListenSocket();
        
        synchronized (this) {
            this.running = true;
            notifyAll();
        }
        
        log.info("Server started!");
        
        while (true) {
            if (Thread.interrupted()) {
                
                break;
            }
            
            log.info("Waiting for connection at {}:{}", inetAddress, port);
            
            acceptNewSocket();
        }
    }
    
    @Override
    public void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                close();
            } catch (Exception e) {
                log.warn("Failed to gracefully stop server", e);
            }
        }, "server-shutdown-thread"));
    }
}
