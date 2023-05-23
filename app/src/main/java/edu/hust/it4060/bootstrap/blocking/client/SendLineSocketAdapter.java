package edu.hust.it4060.bootstrap.blocking.client;

import java.net.Socket;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hust.it4060.common.DelimiterConstants;
import edu.hust.it4060.common.blocking.AbstractBlockingSocketAdapter;

public class SendLineSocketAdapter extends AbstractBlockingSocketAdapter {
    private static final Logger log = LoggerFactory.getLogger(SendLineSocketAdapter.class);
    private static final Scanner SCANNER = new Scanner(System.in);
    private final StringBuilder builder;
    
    public SendLineSocketAdapter(Socket socket) {
        super(socket, DelimiterConstants.DELIMITER);
        builder = new StringBuilder();
    }
    
    @Override
    public void beforeSocketRead() {
        log.info("Connected to server {} from {}", socket.getRemoteSocketAddress(), socket.getLocalSocketAddress());
        try {
            readFromCommandLine();
        } catch (Exception ignore) {
            // ignore
            log.info("Ignoring socket disconnection with {}", socket.getRemoteSocketAddress());
        }
    }
    
    private void readFromCommandLine() {
        System.out.println("Send something to the server...Enter blanking line to disconnect...");
        String input;
        while (!(input = SCANNER.nextLine()).isBlank()) {
            sendString(input);
        }
        closeSocket();
    }
    
    @Override
    public void beforeSocketClosed() {
        if (!builder.isEmpty()) {
            log.info("Server sent something, incase you missed it: {}", builder);
        }
        log.info("Disconnecting...");
    }
    
    @Override
    public void afterSocketClosed() {
        // No op
    }
    
    @Override
    public void newLineRead(String line) {
        builder.append(line);
    }
    
}
