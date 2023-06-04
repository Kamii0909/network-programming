package edu.hust.it4060.homework.week4;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import com.kien.network.core.WebServer;
import com.kien.network.core.support.ArgumentParserUtil;
import com.kien.network.core.support.ArgumentParserUtil.NetworkArguments;
import com.kien.network.core.support.ArgumentParserUtil.ParsingContext;
import com.kien.network.core.support.WebServerFactory;

import edu.hust.it4060.homework.multiplexing.server.TelnetServerSocketAdapterProvider;
import edu.hust.it4060.homework.util.PathUtilities;

public class NoClientTelnetServerMain {
    public static void main(String[] args) throws InterruptedException, IOException {
        NetworkArguments parsedArguments = ArgumentParserUtil.parseArguments(args,
            ParsingContext.createNetworkContext(Collections.emptyMap(), Set.of("credential-file-path")));
        WebServer server = WebServerFactory
            .multiplex(provider(parsedArguments), InetAddress.getLoopbackAddress(), 8080);
        server.addShutdownHook();
        Thread bossThread = server.run();
        bossThread.join();
    }
    
    private static TelnetServerSocketAdapterProvider provider(NetworkArguments arguments) throws IOException {
        String filePath = arguments.getArgument("credential-file-path", null);
        return new TelnetServerSocketAdapterProvider(filePath == null ?
            PathUtilities.getSourceFolderPath(NoClientTelnetServerMain.class.getPackage()).resolve("users.txt") :
            Paths.get(filePath));
    }
}
