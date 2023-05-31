package com.kien.network.core;

/**
 * A Web Server provides services through web interfaces.
 */
public interface WebServer extends AutoCloseable {
    
    /**
     * Run the web server. It is up to implementation to choose the initialization
     * and binding strategy.
     * 
     * @return the boss thread that accept new connections.
     */
    Thread run();
    
    /**
     * Whether to gracefully close the server with a shutdown hook.
     */
    void addShutdownHook();
}
