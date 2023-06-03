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

import edu.hust.it4060.homework.blocking.client.SendLineSocketAdapterProvider;
import edu.hust.it4060.homework.blocking.server.GreetAndLogSocketAdapterProvider;
import edu.hust.it4060.homework.util.BootstrapUtilities;
import edu.hust.it4060.homework.util.PathUtilities;

class Bai12Main {
    public static void main(String[] args) throws IOException {
        NetworkArguments parsedArguments = ArgumentParserUtil.parseArguments(
            args, ParsingContext.createNetworkContext(
                Collections.emptyMap(),
                Set.of("log-file-path", "greeting-file-path")));
        
        InetAddress serverAddress = parsedArguments.getHost();
        int serverPort = parsedArguments.getPort();
        
        try (var server = WebServerFactory.blocking(provider(parsedArguments), serverAddress, serverPort)) {
            
            Thread serverThread = server.run();
            server.addShutdownHook();
            
            BootstrapUtilities.runBlockingClient(true, serverAddress, serverPort, new SendLineSocketAdapterProvider());
            serverThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static GreetAndLogSocketAdapterProvider provider(NetworkArguments parsedArguments) throws IOException {
        Path thisFolderPath = PathUtilities.getSourceFolderPath(Bai12Main.class.getPackage());
        // The greeting.txt file in this folder
        String greetingArg = parsedArguments.getArgument("greeting-file-path", null);
        // The log.txt file in this folder (will be created if not exist)
        String logArg = parsedArguments.getArgument("log-file-path", null);
        
        return new GreetAndLogSocketAdapterProvider(
            greetingArg == null ? thisFolderPath.resolve("greeting.txt") : Paths.get(greetingArg),
            logArg == null ? thisFolderPath.resolve("log.txt") : Paths.get(logArg));
    }
}
