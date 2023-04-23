package edu.hust.it4060.bootstrap.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hust.it4060.common.AbstractWebServer;

class UDPServer extends AbstractWebServer {
    private static final Logger log = LoggerFactory.getLogger(UDPServer.class);
    private ExecutorService executorService;
    private UDPPacketHandler packetHandler;
    private DatagramSocket socket;
    
    public UDPServer(InetAddress inetAddress,
        int port,
        UDPPacketHandler packetHandler,
        ExecutorService executorService) {
        super(inetAddress, port);
        this.packetHandler = packetHandler;
        this.executorService = executorService;
    }
    
    @SuppressWarnings("preview")
    public UDPServer(InetAddress inetAddress, int port, UDPPacketHandler packetHandler) {
        this(inetAddress, port, packetHandler, Executors.newVirtualThreadPerTaskExecutor());
    }
    
    @Override
    protected void startListenSocket() throws IOException {
        socket = new DatagramSocket(port, inetAddress);
    }
    
    @Override
    protected void handleRequests() {
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
        try {
            socket.receive(packet);
            executorService.submit(() -> packetHandler.handle(packet));
        } catch (IOException e) {
            log.error("Unable to receive an UDP packet", e);
        }
        
    }
    
    @Override
    protected void cleanup() {
        executorService.close();
    }
    
}
