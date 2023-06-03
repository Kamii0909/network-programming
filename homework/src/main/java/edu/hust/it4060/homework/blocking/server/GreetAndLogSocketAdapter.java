package edu.hust.it4060.homework.blocking.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kien.network.core.support.adapter.AbstractLineBasedBlockingSocketAdapter;

class GreetAndLogSocketAdapter extends AbstractLineBasedBlockingSocketAdapter {
    private static final Logger log = LoggerFactory.getLogger(GreetAndLogSocketAdapter.class);
    private static FileWriter writer;
    private static String greeting;
    
    // Checked exception oh my fucking god
    public static void initialize(Path greetingFilePath, Path logFilePath) throws IOException {
        File logFile = logFilePath.toFile();
        if (logFile.createNewFile()) {
            log.info("Created log file at {}", logFile.getAbsolutePath());
        }
        GreetAndLogSocketAdapter.writer = new FileWriter(logFile, true);
        GreetAndLogSocketAdapter.greeting = Files.readString(greetingFilePath);
    }
    
    private final StringBuilder builder;
    
    public GreetAndLogSocketAdapter() {
        builder = new StringBuilder();
    }
    
    @Override
    public void onReady() {
        log.info("Started handling request from {}", getRemoteSocketAddress());
        sendLine(greeting);
        builder.append(getRemoteSocketAddress() + " sent:\r\n");
    }
    
    @Override
    public void newLineRead(String line) {
        builder.append(line).append("\r\n");
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
    
}
