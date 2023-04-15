package edu.hust.it4060.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;

public class ArgumentsUtillities {
    private ArgumentsUtillities() {
    }
    
    public static class ArgumentsHolder {
        private final InetAddress serverAddress;
        
        public ArgumentsHolder(InetAddress serverAddress, int serverPort, String greetingFilePath, String logFilePath) {
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
            this.greetingFilePath = greetingFilePath;
            this.logFilePath = logFilePath;
        }
        
        private final int serverPort;
        private final String greetingFilePath;
        private final String logFilePath;
        
        public InetAddress getServerAddress() {
            return serverAddress;
        }
        
        public int getServerPort() {
            return serverPort;
        }
        
        public String getGreetingFilePath() {
            return greetingFilePath;
        }
        
        public String getLogFilePath() {
            return logFilePath;
        }
        
    }
    
    public static ArgumentsHolder parseArgsHW1(String[] args, Logger logger) {
        
        InetAddress serverAddress = InetAddress.getLoopbackAddress();
        int serverPort = 8080;
        String greetingFilePath = "./greeting.txt";
        String logFilePath = "./log.txt";
        
        switch (args.length) {
        case 4:
            return new ArgumentsHolder(
                parseInetAddressWithLog(args[0], serverAddress, logger),
                parseIntegerWithLog(args[1], serverPort, logger),
                args[2],
                args[3]);
        
        case 2:
            return new ArgumentsHolder(
                parseInetAddressWithLog(args[0], serverAddress, logger),
                parseIntegerWithLog(args[1], serverPort, logger),
                greetingFilePath,
                logFilePath);
        
        case 3:
            return new ArgumentsHolder(
                serverAddress,
                parseIntegerWithLog(args[0], serverPort, logger),
                args[1],
                args[2]);
        
        default:
            return new ArgumentsHolder(serverAddress, serverPort, greetingFilePath, logFilePath);
        }
    }
    
    public static InetAddress parseInetAddressWithLog(String string, InetAddress fallback, Logger logger) {
        try {
            return InetAddress.getByName(string);
        } catch (UnknownHostException e) {
            logger.warn("""
                    Parsed unknown host {}.
                    Falling back to {}...
                """, string, fallback, e);
            return fallback;
        }
    }
    
    public static int parseIntegerWithLog(String string, int fallback, Logger logger) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            logger.warn("""
                    Parsed unknown integer {}.
                    Falling back to {}...
                """, string, fallback, e);
            return fallback;
        }
    }
}
