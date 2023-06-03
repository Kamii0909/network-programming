package edu.hust.it4060.homework.week1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kien.network.core.support.WebServerFactory;

import edu.hust.it4060.homework.blocking.client.MockSendLineSocketAdapterProvider;
import edu.hust.it4060.homework.blocking.server.GreetAndLogSocketAdapterProvider;
import edu.hust.it4060.homework.util.BootstrapUtilities;
import edu.hust.it4060.homework.util.ConsumerCapturer;
import edu.hust.it4060.homework.util.TestPathUtilities;

class Bai12Test {
    
    @BeforeEach
    void resetLogFile() throws IOException {
        Path logFilePath = TestPathUtilities.getSourceFolderPath(Bai12Test.class.getPackage()).resolve("log.txt");
        Files.write(logFilePath, new byte[0]);
    }
    
    @Test
    void integrationTest() throws Exception {
        Path thisFolderPath = TestPathUtilities.getSourceFolderPath(Bai12Test.class.getPackage());
        Path greetingFilePath = thisFolderPath.resolve("greeting.txt");
        Path logFilePath = thisFolderPath.resolve("log.txt");
        
        String sentFromServer = "Hello from Server";
        String sentFromClient = "Test line from client";
        
        assertEquals(sentFromServer, Files.readString(thisFolderPath.resolve(greetingFilePath)));
        
        WebServerFactory.blocking(
            new GreetAndLogSocketAdapterProvider(greetingFilePath, logFilePath),
            InetAddress.getLoopbackAddress(), 8080).run();
        
        ConsumerCapturer<String> capturer = new ConsumerCapturer<>();
        BootstrapUtilities.runBlockingClient(true,
            InetAddress.getLoopbackAddress(), 8080,
            new MockSendLineSocketAdapterProvider(sentFromClient, capturer));
        
        assertEquals(sentFromServer, capturer.getCaptured());
        Awaitility.await().until(() -> Files.readString(logFilePath).contains(sentFromClient));
    }
}
