package edu.hust.it4060.common.server;

import java.net.InetAddress;

import edu.hust.it4060.bootstrap.blocking.server.BlockingTCPServer;
import edu.hust.it4060.common.WebServer;
import edu.hust.it4060.common.socket.BlockingSocketHandler;
import edu.hust.it4060.common.socket.SocketHandler;

/**
 * The primary way to create TCP Server
 */
public class WebServerFactory {
    private WebServerFactory() {
    }

    public static WebServer create(
            SocketHandler socketHandler,
            InetAddress inetAddress, int port,
            IOStrategy ioStrategy) {
        switch (ioStrategy) {
            case BLOCKING:
                if (socketHandler instanceof BlockingSocketHandler bsh) {
                    return new BlockingTCPServer(inetAddress, port, bsh);
                } else {
                    throw new IllegalArgumentException(
                            "The given socket handler is not compatible for " + ioStrategy);
                }

            default:
                throw new UnsupportedOperationException();

        }
    }
}
