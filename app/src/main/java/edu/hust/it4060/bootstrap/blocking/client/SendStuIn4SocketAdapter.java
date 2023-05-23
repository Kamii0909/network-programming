package edu.hust.it4060.bootstrap.blocking.client;

import java.net.Socket;
import java.time.LocalDate;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hust.it4060.common.DelimiterConstants;
import edu.hust.it4060.common.blocking.AbstractBlockingSocketAdapter;

public class SendStuIn4SocketAdapter extends AbstractBlockingSocketAdapter {
    private static final Logger log = LoggerFactory.getLogger(SendStuIn4SocketAdapter.class);
    private static final Scanner SCANNER = new Scanner(System.in);
    private final StringBuilder builder;
    
    public SendStuIn4SocketAdapter(Socket socket) {
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
        System.out.println("""
            Input student information to send...
            Each line consists of {MSSV} {Ho ten} {Ngay sinh: yyyy/mm/dd} {Diem trung binh}
            Example: 20190078 Ha Trung Kien 2001-08-09 0.0
            You can send multiple lines, invalid ones are discarded, enter blank line to terminate
            """);
        String input;
        while (!(input = SCANNER.nextLine()).isBlank()) {
            if (isValidStudentInformation(input)) {
                sendString(input);
            } else {
                System.out.println("Not a valid student information!");
            }
        }
        closeSocket();
    }
    
    private static boolean isValidStudentInformation(String line) {
        String[] args = line.split(" ");
        int i = args.length;
        if (i < 4)
            return false;
        try {
            Integer.parseInt(args[0]);
            LocalDate.parse(args[i - 2]);
            Double.parseDouble(args[i - 1]);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
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
