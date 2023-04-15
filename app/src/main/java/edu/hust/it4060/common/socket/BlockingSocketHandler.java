package edu.hust.it4060.common.socket;

import java.net.Socket;

@FunctionalInterface
public non-sealed interface BlockingSocketHandler extends SocketHandler{
    /**
     * Handle the accepted Socket synchronously. 
     */
    public void handle(Socket socket);
}
