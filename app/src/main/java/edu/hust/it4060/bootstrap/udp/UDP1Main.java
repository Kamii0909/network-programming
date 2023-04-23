package edu.hust.it4060.bootstrap.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Scanner;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hust.it4060.common.WebServer;
import edu.hust.it4060.util.ArgumentParserUtil;
import edu.hust.it4060.util.ArgumentParserUtil.NetworkArguments;
import edu.hust.it4060.util.ArgumentParserUtil.ParsingContext;

class UDP1Main {
    
    private static class BlockingSysOutPacketHandler implements UDPPacketHandler {
        
        @Override
        public void handle(DatagramPacket packet) {
            String data = new String(packet.getData(), StandardCharsets.UTF_8);
            System.out.println(data);
        }
        
    }
    
    private static class UDPClient {
        private static final Scanner SCANNER = new Scanner(System.in);
        private static final Logger log = LoggerFactory.getLogger(UDPClient.class);
        private final InetAddress serverAddress;
        private final int serverPort;
        private final DatagramSocket socket;
        
        public UDPClient(InetAddress serverAddress, int serverPort) throws SocketException {
            socket = new DatagramSocket();
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
            
        }
        
        public void handle() {
            System.out.println("Send computer information to the Server at %s:%s".formatted(serverAddress, serverPort));
            // System.out.println("Any blank line terminate the session.");
            
            StringJoiner builder = new StringJoiner(" ");
            
            builder.add(readComputerName());
            System.out.print("Enter the amount of disks: ");
            int num = Integer.parseInt(SCANNER.nextLine());
            
            for (int i = 0; i < num; i++) {
                System.out.print("Enter the next disk name: ");
                builder.add(SCANNER.nextLine());
                System.out.print("Enter the disk size: ");
                builder.add(SCANNER.nextLine());
            }
            
            System.out.println("""
                Do you want to send the following information to server?
                Type 1 to send, anything else will abort""");
            System.out.println(builder.toString());
            
            if (SCANNER.nextLine().equals("1")) {
                send(builder.toString());
                System.out.println("Sent");
            } else {
                System.out.println("Aborted");
            }
        }
        
        private String readComputerName() {
            String name;
            do {
                System.out.print("Enter your computer name: ");
                name = SCANNER.nextLine();
            } while (name.isBlank());
            return name;
        }
        
        private void send(String string) {
            try {
                byte[] data = string.getBytes(StandardCharsets.UTF_8);
                socket.send(new DatagramPacket(data, data.length, serverAddress, serverPort));
            } catch (IOException e) {
                log.error("Unable to send data {}", string, e);
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        NetworkArguments networkInfo = ArgumentParserUtil.parseArguments(args, getParsingContext());
        @SuppressWarnings("java:S2095") //
        WebServer server =
            new UDPServer(networkInfo.getHost(), networkInfo.getPort(), new BlockingSysOutPacketHandler());
        Thread serverThread = server.run();
        
        UDPClient client = new UDPClient(networkInfo.getHost(), networkInfo.getPort());
        client.handle();
        
        serverThread.join();
    }
    
    private static ParsingContext getParsingContext() {
        return ParsingContext.createNetworkContext(Collections.emptyMap(), Collections.emptySet());
    }
    
}
