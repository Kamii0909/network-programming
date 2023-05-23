package edu.hust.it4060.bootstrap.udp;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.hust.it4060.common.WebServer;
import edu.hust.it4060.util.ArgumentParserUtil;
import edu.hust.it4060.util.ArgumentParserUtil.NetworkArguments;
import edu.hust.it4060.util.ArgumentParserUtil.ParsingContext;

public class UDP2Main {
    
    private static class FileTransferPacketHandler implements UDPPacketHandler {
        private static final Logger log = LoggerFactory.getLogger(FileTransferPacketHandler.class);
        private final Path resultDirectory;
        
        public FileTransferPacketHandler(String resultDirectory) {
            this.resultDirectory = Path.of(resultDirectory);
            try {
                Files.createDirectories(this.resultDirectory);
            } catch (IOException e) {
                log.info("Exception in creating result directory", e);
            }
        }
        
        @Override
        public void handle(DatagramPacket packet) {
            byte[] data = packet.getData();
            
            // Network protocol typically uses big endian
            String fileName = new String(data, 3, data[0], StandardCharsets.UTF_8);
            // The first one is file name
            File file = new File(resultDirectory.toFile(), fileName);
            if (file.exists() && file.isDirectory()) {
                log.warn("The user submitted path {} is a directory!", file.getPath());
                return;
            }
            OutputStream writer = null;
            try {
                if (file.createNewFile()) {
                    log.info("Created a new file at {}", file.getPath());
                }
                writer = new FileOutputStream(file, true);
                writer.write(data, 3 + data[0], data[1] << Byte.SIZE | (data[2] & 0xFF));
                writer.flush();
                
            } catch (IOException e) {
                log.warn("Error writing to file {}", file.getPath(), e);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        log.error("Fatal error: Unable to close file output stream for {}", file.getAbsolutePath(), e);
                    }
                }
            }
        }
    }
    
    private static class UDPClient {
        private static final Logger log = LoggerFactory.getLogger(UDPClient.class);
        private final InetAddress serverAddress;
        private final int serverPort;
        private final File file;
        private final String targetFile;
        private final DatagramSocket socket;
        
        public UDPClient(InetAddress serverAddress, int serverPort, String sourceFile, String targetFile)
            throws SocketException {
            socket = new DatagramSocket();
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
            this.file = new File(sourceFile);
            this.targetFile = targetFile;
        }
        
        public void connectAndSend() {
            if (!file.exists()) {
                log.warn("File at {} does not exist, sending nothing", file.getPath());
                return;
            }
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                send(fileInputStream.readAllBytes());
            } catch (IOException e) {
                log.error("Unable to read from file at {}", file.getAbsolutePath());
            }
        }
        
        private void send(byte[] input) {
            try {
                byte[] targetByte = targetFile.getBytes();
                byte[] data = new byte[3 + targetByte.length + input.length];
                
                if (targetByte.length >= 256) {
                    log.warn("Target file name too long, skipping");
                    return;
                } else if (input.length >= 1024) {
                    log.warn("""
                        File content is too large for one transfer, skipping.
                        UDP is not suitable for reliable chunk transfer.""");
                    return;
                }
                // We use the first byte to tell how many bytes does target file name takes
                // My machine is an Intel with big endian already.
                data[0] = (byte) (targetByte.length & 0xFF);
                
                // We use 2 bytes for file content
                data[2] = (byte) (input.length & 0xFF);
                data[1] = (byte) (input.length >> Byte.SIZE & 0xFF);
                
                System.arraycopy(targetByte, 0, data, 3, targetByte.length);
                System.arraycopy(input, 0, data, 3 + targetByte.length, input.length);
                
                socket.send(new DatagramPacket(data, data.length, serverAddress, serverPort));
                
                log.atInfo().addArgument(new String(data, UTF_8)).log("Sent to server: {}");
            } catch (IOException e) {
                log.error("Unable to send data {}", input, e);
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException, SocketException {
        NetworkArguments networkInfo = ArgumentParserUtil.parseArguments(args, getParsingContext());
        
        @SuppressWarnings("java:S2095") //
        WebServer server = new UDPServer(
            networkInfo.getHost(),
            networkInfo.getPort(),
            new FileTransferPacketHandler(networkInfo.getArgument("server-file-path", "./udp/files")));
        
        Thread serverThread = server.run();
        
        int numberOfClients = Integer.parseInt(networkInfo.getArgument("number-of-clients", "10"));
        CountDownLatch countDownLatch = new CountDownLatch(numberOfClients);

        class Worker extends Thread {
            CountDownLatch latch;
            
            Worker(CountDownLatch latch, Runnable runnable) {
                super(runnable);
                this.latch = latch;
            }
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                super.run();
            }
        }

        for (int i = 0; i < numberOfClients; i++) {
            UDPClient client = new UDPClient(
            networkInfo.getHost(),
            networkInfo.getPort(),
            networkInfo.getArgument("client-source-file-path", "greeting.txt"),
            networkInfo.getArgument("client-target-file-path", "myfile%d.txt".formatted(i)));
            Worker worker = new Worker(countDownLatch, client::connectAndSend);
            worker.start();
            countDownLatch.countDown();
        }
        
        serverThread.join();
    }
    
    private static ParsingContext getParsingContext() {
        return ParsingContext.createNetworkContext(
            Collections.emptyMap(),
            Set.of("server-result-directory", "client-source-file-path", "client-target-file-path",
                "number-of-clients"));
    }
}
