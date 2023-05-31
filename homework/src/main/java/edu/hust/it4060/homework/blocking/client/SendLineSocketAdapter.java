package edu.hust.it4060.homework.blocking.client;

import java.util.Scanner;

import com.kien.network.core.DelimiterConstants;
import com.kien.network.core.support.adapter.AbstractLineBasedSocketAdapter;

public class SendLineSocketAdapter extends AbstractLineBasedSocketAdapter {
    private static final Scanner SCANNER = new Scanner(System.in);
    private final StringBuilder builder;
    
    public SendLineSocketAdapter() {
        super(DelimiterConstants.DELIMITER);
        builder = new StringBuilder();
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
    
    private void readFromCommandLine() {
        System.out.println("Send something to the server...Enter blanking line to disconnect...");
        String input;
        while (!(input = SCANNER.nextLine()).isBlank()) {
            sendLine(input);
        }
        closeSocket();
    }
    
}
