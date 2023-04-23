package edu.hust.it4060.common;

import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWebServer implements WebServer {
    private static final Logger log = LoggerFactory.getLogger(AbstractWebServer.class);
    protected final InetAddress inetAddress;
    protected final int port;
    private Thread bossThread;
    private boolean running;
    
    protected AbstractWebServer(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }
    
    @Override
    @SuppressWarnings("preview")
    public Thread run() {
        bossThread = Thread
            .ofPlatform()
            .daemon()
            .name("Server Thread")
            .unstarted(() ->
            {
                try {
                    startBossThread();
                } catch (IOException e) {
                    log.info("Server cannot start...", e);
                }
            });
            
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
    
    protected abstract void startListenSocket() throws IOException;
    
    /**
     * The server will block calling this, meaning any thread pooling or asynchrony
     * happens inside this method.
     */
    protected abstract void handleRequests();
    
    protected abstract void cleanup() throws IOException;
    
    private void startBossThread() throws IOException {
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
            handleRequests();
        }
        
    }
}
