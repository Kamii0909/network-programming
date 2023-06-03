package edu.hust.it4060.homework.blocking.client;

import java.util.Scanner;

import com.kien.network.core.support.adapter.AbstractLineBasedBlockingSocketAdapter;

class SendLineSocketAdapter extends AbstractLineBasedBlockingSocketAdapter {
    private static final Scanner SCANNER = new Scanner(System.in);
    private final StringBuilder builder;
    
    public SendLineSocketAdapter() {
        builder = new StringBuilder();
    }
    
    public String getServerSentString() {
        return builder.toString();
    }
    
    @Override
    public void newLineRead(String line) {
        builder.append(line);
    }
    
    @Override
    protected void onReady() {
        try {
            readFromCommandLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onClosed() {
        readAvailableLines();
        System.out.println("Server sent something, in case you missed it: " + builder);
    }
    
    private void readFromCommandLine() {
        System.out.println("Send something to the server...Enter blanking line to disconnect...");
        String input;
        while (!(input = SCANNER.nextLine()).isBlank()) {
            sendLine(input);
        }
        closeSocket();
    }
    
}
