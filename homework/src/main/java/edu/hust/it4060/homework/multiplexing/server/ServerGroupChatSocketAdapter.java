package edu.hust.it4060.homework.multiplexing.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.kien.network.core.support.adapter.AbstractLineBasedSocketChannelAdapter;

class ServerGroupChatSocketAdapter extends AbstractLineBasedSocketChannelAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerGroupChatSocketAdapter.class);
    private static final List<ServerGroupChatSocketAdapter> clients =
        Collections.synchronizedList(new ArrayList<>());
    private static Splitter splitter = Splitter.on(": ");
    private boolean connected;
    private String username;
    private String userId;
    
    @Override
    protected void onFinished() {
        clients.remove(this);
    }
    
    @Override
    protected void onReady() {
        clients.add(this);
        sendLine("Login with [userid: username]. For example: HTK: Ha Trung Kien");
    }
    
    protected void newLineRead(String newLine) {
        if (connected) {
            sendToOthers(userId + ": " + newLine);
            log.info("Client {} with id {} sent: {}", username, userId, newLine);
        } else if (!login(newLine)) {
            sendLine("Invalid login. Login with [userid: username].");
        }
    }
    
    private boolean login(String line) {
        List<String> splitToList = splitter.splitToList(line);
        if (splitToList.size() != 2) {
            return false;
        }
        this.userId = splitToList.get(0);
        this.username = splitToList.get(1);
        this.connected = true;
        return true;
    }
    
    private void sendToOthers(String line) {
        clients.stream().filter(sa -> !sa.equals(this)).forEach(sa -> sa.sendLine(line));
    }
}
