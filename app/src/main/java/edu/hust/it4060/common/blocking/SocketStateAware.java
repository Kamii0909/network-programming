package edu.hust.it4060.common.blocking;

public interface SocketStateAware {
    /**
     * Hook to be called before any I/O operation is initiated (after handshake).
     */
    void beforeSocketRead();

    /**
     * Hook to be called just before socket is closed.
     */
    void beforeSocketClosed();

    /**
     * Hook to be called after socket is closed.
     */
    void afterSocketClosed();

    /**
     * Initiate a socket close sequence.
     */
    void closeSocket();
}
