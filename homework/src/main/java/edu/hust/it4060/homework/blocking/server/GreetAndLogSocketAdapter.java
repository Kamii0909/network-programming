package edu.hust.it4060.homework.blocking.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kien.network.core.DelimiterConstants;
import com.kien.network.core.support.adapter.AbstractLineBasedSocketAdapter;

public class GreetAndLogSocketAdapter extends AbstractLineBasedSocketAdapter {
    private static final Logger log = LoggerFactory.getLogger(GreetAndLogSocketAdapter.class);
    private static FileWriter writer;
    private static String greeting;
    
    // Checked exception oh my fucking god
    public static void initialize(String greetingFilePath, String logFilePath) throws IOException {
        File logFile = Paths.get(logFilePath).toFile();
        log.info("Greeting file path is: {}", Paths.get(greetingFilePath).toAbsolutePath());
        log.info("Log file path is: {}", logFile.getAbsolutePath());
        
        if (logFile.createNewFile()) {
            log.info("Created log file at {}", logFile.getAbsolutePath());
        }
        
        GreetAndLogSocketAdapter.writer = new FileWriter(logFile, true);
        GreetAndLogSocketAdapter.greeting = Files.readString(Paths.get(greetingFilePath));
        
        log.info("Greeting: {}", greeting);
    }
    
    private final StringBuilder builder = new StringBuilder();
    
    public GreetAndLogSocketAdapter() {
        super(DelimiterConstants.DELIMITER);
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
