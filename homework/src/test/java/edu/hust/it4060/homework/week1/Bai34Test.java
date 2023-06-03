package edu.hust.it4060.homework.week1;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.kien.network.core.support.WebServerFactory;

import edu.hust.it4060.homework.blocking.client.MockSendStuIn4SocketAdapterProvider;
import edu.hust.it4060.homework.blocking.server.GetStuIn4SocketAdapterProvider;
import edu.hust.it4060.homework.util.BootstrapUtilities;
import edu.hust.it4060.homework.util.TestPathUtilities;

class Bai34Test {
    @BeforeEach
    void resetLogFile() throws IOException {
        Path logFilePath = TestPathUtilities.getSourceFolderPath(Bai34Test.class.getPackage()).resolve("log.txt");
        Files.write(logFilePath, new byte[0]);
    }
    
    @Test
    void integration() throws IOException {
        Path thisFolderPath = TestPathUtilities.getSourceFolderPath(Bai34Test.class.getPackage());
        Path logFilePath = thisFolderPath.resolve("log.txt");
        
        WebServerFactory.blocking(
            new GetStuIn4SocketAdapterProvider(logFilePath),
            InetAddress.getLoopbackAddress(), 8080).run();
        
        BootstrapUtilities.runBlockingClient(true, InetAddress.getLoopbackAddress(), 8080,
            new MockSendStuIn4SocketAdapterProvider());
        
        Awaitility.await().until(() -> Files.readString(logFilePath).contains("Ha Trung Kien"));
    }
}
