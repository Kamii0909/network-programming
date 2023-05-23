package edu.hust.it4060.bootstrap.blocking.server;

import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hust.it4060.common.DelimiterConstants;
import edu.hust.it4060.common.blocking.AbstractBlockingSocketAdapter;

public class GreetAndLogSocketAdapter extends AbstractBlockingSocketAdapter {
    private static final Logger log = LoggerFactory.getLogger(GreetAndLogSocketAdapter.class);
    private static FileWriter writer;
    private static String greeting;
    
    // Checked exception oh my fucking god
    public static void initialize(String greetingFilePath, String logFilePath) throws IOException {
        GreetAndLogSocketAdapter.writer = new FileWriter(logFilePath, true);
        GreetAndLogSocketAdapter.greeting = Files.readString(Paths.get(greetingFilePath));
    }
    
    private final StringBuilder builder = new StringBuilder();
    
    public GreetAndLogSocketAdapter(Socket socket) {
        super(socket, DelimiterConstants.DELIMITER);
    }
    
    @Override
    public void beforeSocketRead() {
        log.info("Started handling request from {}", socket.getRemoteSocketAddress());
        sendString(greeting);
        builder.append(socket.getRemoteSocketAddress() + " sent:\r\n");
    }
    
    @Override
    public void afterSocketClosed() {
        log.info("Completed handling request from {}", socket.getRemoteSocketAddress());
    }
    
    @Override
    public void newLineRead(String line) {
        builder.append(line).append("\r\n");
    }
    
    @Override
    public void beforeSocketClosed() {
        try {
            writer.write(builder.toString());
            writer.flush();
        } catch (IOException e) {
            log.error("Error writing to file", e);
        }
    }
    
}
