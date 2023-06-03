package edu.hust.it4060.homework.week1;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import com.kien.network.core.support.ArgumentParserUtil;
import com.kien.network.core.support.ArgumentParserUtil.NetworkArguments;
import com.kien.network.core.support.ArgumentParserUtil.ParsingContext;
import com.kien.network.core.support.WebServerFactory;

import edu.hust.it4060.homework.blocking.client.SendStuIn4SocketAdapterProvider;
import edu.hust.it4060.homework.blocking.server.GetStuIn4SocketAdapterProvider;
import edu.hust.it4060.homework.util.BootstrapUtilities;
import edu.hust.it4060.homework.util.PathUtilities;

class Bai34Main {
    public static void main(String[] args) {
        NetworkArguments parsedArguments = ArgumentParserUtil.parseArguments(args,
            ParsingContext.createNetworkContext(Collections.emptyMap(), Set.of("log-file-path")));
        
        InetAddress serverAddress = parsedArguments.getHost();
        int serverPort = parsedArguments.getPort();
        
        try (var server = WebServerFactory.blocking(provider(parsedArguments), serverAddress, serverPort)) {
            Thread serverThread = server.run();
            server.addShutdownHook();
            
            BootstrapUtilities.runBlockingClient(true, serverAddress, serverPort,
                new SendStuIn4SocketAdapterProvider());
            
            serverThread.join();
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private static GetStuIn4SocketAdapterProvider provider(NetworkArguments parsedArguments) throws IOException {
        String logArg = parsedArguments.getArgument("log-file-path", null);
        Path thisFolderPath = PathUtilities.getSourceFolderPath(Bai34Main.class.getPackage());
        return new GetStuIn4SocketAdapterProvider(
            logArg == null ? thisFolderPath.resolve("log.txt") : Paths.get(logArg));
    }
}
