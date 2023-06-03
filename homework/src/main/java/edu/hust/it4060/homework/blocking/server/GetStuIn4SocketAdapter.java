package edu.hust.it4060.homework.blocking.server;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kien.network.core.support.adapter.AbstractLineBasedBlockingSocketAdapter;

class GetStuIn4SocketAdapter extends AbstractLineBasedBlockingSocketAdapter {
    private static final Logger log = LoggerFactory.getLogger(GetStuIn4SocketAdapter.class);
    private static FileWriter writer;
    
    // Checked exception oh my fucking god
    public static void initialize(Path logFilePath) throws IOException {
        GetStuIn4SocketAdapter.writer = new FileWriter(logFilePath.toFile(), true);
    }
    
    private final StringBuilder builder;
    
    public GetStuIn4SocketAdapter() {
        builder = new StringBuilder();
    }
    
    @Override
    public void onFinished() {
        try {
            writer.write(builder.toString());
            writer.flush();
        } catch (IOException e) {
            log.error("Error writing to file", e);
        }
    }
    
    public void newLineRead(String line) {
        builder
            .append(getRemoteSocketAddress()).append(" ")
            .append(LocalDateTime.now()).append(" ")
            .append(line).append("\r\n");
    }
}
