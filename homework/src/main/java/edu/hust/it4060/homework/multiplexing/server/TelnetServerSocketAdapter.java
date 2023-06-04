package edu.hust.it4060.homework.multiplexing.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;
import com.kien.network.core.support.ExceptionUtils;
import com.kien.network.core.support.adapter.AbstractLineBasedSocketChannelAdapter;

class TelnetServerSocketAdapter extends AbstractLineBasedSocketChannelAdapter {
    private static final ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();
    private static final Splitter SPLITTER = Splitter.on(": ");
    
    public static void initialize(Path usersFilePath) throws IOException {
        Splitter splitter = Splitter.on(" ");
        Map<String, String> newUsers = Files.readAllLines(usersFilePath)
            .stream()
            .map(str ->
            {
                List<String> list = splitter.splitToList(str);
                if (list.size() != 2) {
                    throw new IllegalArgumentException("Found invalid user declaration: " + str);
                }
                return list;
            })
            .collect(Collectors.toMap(user -> user.get(0), user -> user.get(1)));
        users.putAll(newUsers);
    }
    
    private boolean loggedin;
    private String username;
    
    @Override
    protected void onReady() {
        sendLine("Log in with [username: password]. For example: kienht: kienht");
    }
    
    @Override
    protected void newLineRead(String newLine) {
        if (loggedin) {
            ProcessBuilder pb = new ProcessBuilder(newLine);
            File logFile = new File("logs/" + username + ".out");
            ExceptionUtils.unlikely(logFile::createNewFile, Throwable::printStackTrace);
            pb.redirectOutput(logFile);
            try {
                Process process = pb.start();
                process.waitFor();
            } catch (InterruptedException e) {
                // We don't need this alert, it's probably Selector close signal
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                sendLine("Error trying to accomplish the command.");
            }
            
            ExceptionUtils.unlikely(() -> sendMultiLineString(Files.readString(logFile.toPath())), Throwable::printStackTrace);
        } else if (!login(newLine)) {
            sendLine("Invalid credentials.");
        } else {
            sendLine("Logged in succesfully. Send a terminal command.");
        }
    }
    
    private boolean login(String line) {
        List<String> credential = SPLITTER.splitToList(line);
        if (credential.size() != 2) {
            return false;
        }
        if (!credential.get(1).equals(users.get(credential.get(0)))) {
            return false;
        }
        this.loggedin = true;
        this.username = credential.get(0);
        return true;
    }
}
