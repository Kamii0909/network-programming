package edu.hust.it4060.common.client;

public interface TCPClient extends AutoCloseable {
    /**
     * Connect to the Server. When, how and where to connect and what to do with the connection is fully up to
     * implementations. 
     */
    public void connect();
}
