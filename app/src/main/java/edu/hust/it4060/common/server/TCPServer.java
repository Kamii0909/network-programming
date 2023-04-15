package edu.hust.it4060.common.server;

public interface TCPServer extends AutoCloseable {
    /**
     * Start the Server. This method returns after Server is ready to accept
     * requests. When, how and where to connect and how to handle new connection is
     * up to implementations.
     * 
     * @return the server thread (typically the boss thread that accept new
     *         connections).
     */
    public Thread start();

    /**
     * Add a shutdown hook that close the Server with JVM.
     * @return
     */
    public void addShutdownHook();
}
