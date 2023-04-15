package edu.hust.it4060.blocking.server;

import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hust.it4060.common.DelimiterConstants;
import edu.hust.it4060.common.blocking.AbstractBlockingSocketAdapter;

public class GetStuIn4SocketAdapter extends AbstractBlockingSocketAdapter {
    private static final Logger log = LoggerFactory.getLogger(GetStuIn4SocketAdapter.class);
    private static FileWriter writer;
    
    // Checked exception oh my fucking god
    public static void initialize(String logFilePath) throws IOException {
        GetStuIn4SocketAdapter.writer = new FileWriter(logFilePath, true);
    }
    
    private final StringBuilder builder = new StringBuilder();
    
    public GetStuIn4SocketAdapter(Socket socket) {
        super(socket, DelimiterConstants.DELIMITER);
    }
    
    @Override
    public void beforeSocketRead() {
        log.info("Started handling request from {}", socket.getRemoteSocketAddress());
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
    
    @Override
    public void afterSocketClosed() {
        log.info("Completed handling request from {}", socket.getRemoteSocketAddress());
        
    }
    
    @Override
    public void newLineRead(String line) {
        builder
            .append(socket.getRemoteSocketAddress()).append(" ")
            .append(LocalDateTime.now()).append(" ")
            .append(line).append("\r\n");
    }
    
}
