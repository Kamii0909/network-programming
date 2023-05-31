package edu.hust.it4060.homework.blocking.client;

import java.time.LocalDate;
import java.util.Scanner;

import com.kien.network.core.DelimiterConstants;
import com.kien.network.core.support.adapter.AbstractLineBasedSocketAdapter;

public class SendStuIn4SocketAdapter extends AbstractLineBasedSocketAdapter {
    private static final Scanner SCANNER = new Scanner(System.in);
    private final StringBuilder builder;
    
    public SendStuIn4SocketAdapter() {
        super(DelimiterConstants.DELIMITER);
        builder = new StringBuilder();
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
                sendLine(input);
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
    
}
