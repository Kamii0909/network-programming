package com.kien.network.core;

/**
 * A client is capable of connecting to a WebServer through TCP Socket.
 */
public interface WebClient extends AutoCloseable {
    /**
     * Connect to the Server through a TCP Connection. When, how and where to
     * connect and what to do with the connection is fully up to implementations.
     */
    public void connect();
}
