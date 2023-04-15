package edu.hust.it4060.common.blocking;

import java.net.Socket;

public abstract class BlockingSocketAdapter {
    protected final Socket socket;

    protected BlockingSocketAdapter(Socket socket) {
        this.socket = socket;
    }

    /**
     * Handle the accepted socket.
     */
    protected abstract void handle();
}
