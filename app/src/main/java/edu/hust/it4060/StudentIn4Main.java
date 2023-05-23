package edu.hust.it4060;

import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hust.it4060.bootstrap.blocking.client.SendStuIn4SocketAdapter;
import edu.hust.it4060.bootstrap.blocking.server.GetStuIn4SocketAdapter;
import edu.hust.it4060.common.ArgumentsUtillities;
import edu.hust.it4060.common.WebServer;
import edu.hust.it4060.common.client.TCPClient;
import edu.hust.it4060.common.client.TCPClientFactory;
import edu.hust.it4060.common.server.IOStrategy;
import edu.hust.it4060.common.server.WebServerFactory;
import edu.hust.it4060.common.socket.BlockingSocketHandler;

public class StudentIn4Main {
    private static final Logger log = LoggerFactory.getLogger(StudentIn4Main.class);
    
    @SuppressWarnings({ "java:S2142", "java:S4087" })
    public static void main(String[] args) throws IOException {
        ArgumentsUtillities.ArgumentsHolder holder = ArgumentsUtillities.parseArgsHW1(args, log);
        InetAddress serverAddress = holder.getServerAddress();
        int serverPort = holder.getServerPort();
        
        GetStuIn4SocketAdapter.initialize(holder.getLogFilePath());
        
        try (
            WebServer server = WebServerFactory.create(
                (BlockingSocketHandler) s -> new GetStuIn4SocketAdapter(s).handle(),
                serverAddress, serverPort, IOStrategy.BLOCKING);
            TCPClient client = TCPClientFactory.create(
                (BlockingSocketHandler) s -> new SendStuIn4SocketAdapter(s).handle(),
                serverAddress, serverPort, IOStrategy.BLOCKING);) {
            
            Thread serverThread = server.run();
            server.addShutdownHook();
            
            client.connect();
            client.close();
            
            serverThread.join();
        } catch (Exception e) {
            log.error("Application error", e);
        }
    }
}
