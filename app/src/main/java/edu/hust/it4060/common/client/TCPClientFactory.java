package edu.hust.it4060.common.client;

import java.net.InetAddress;

import edu.hust.it4060.bootstrap.blocking.client.BlockingTCPClient;
import edu.hust.it4060.common.server.IOStrategy;
import edu.hust.it4060.common.socket.BlockingSocketHandler;
import edu.hust.it4060.common.socket.SocketHandler;

public class TCPClientFactory {
    private TCPClientFactory() {
    }

    public static TCPClient create(
            SocketHandler socketHandler,
            InetAddress inetAddress, int port,
            IOStrategy ioStrategy) {
        switch (ioStrategy) {
            case BLOCKING:
                if (socketHandler instanceof BlockingSocketHandler bsh) {
                    return new BlockingTCPClient(inetAddress, port, bsh);
                } else {
                    throw new IllegalArgumentException(
                            "The given socket handler is not compatible for " + ioStrategy);
                }

            default:
                throw new UnsupportedOperationException();
        }
    }
}
