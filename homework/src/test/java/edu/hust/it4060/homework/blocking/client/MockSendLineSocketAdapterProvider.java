package edu.hust.it4060.homework.blocking.client;

import java.net.Socket;
import java.util.function.Consumer;

import com.kien.network.core.socket.api.adapter.BlockingSocketAdapter;
import com.kien.network.core.socket.api.adapter.BlockingSocketAdapterProvider;

public class MockSendLineSocketAdapterProvider implements BlockingSocketAdapterProvider {
    private final String lineToSend;
    private final Consumer<String> onClosedVerification;
    
    public MockSendLineSocketAdapterProvider(String lineToSend, Consumer<String> onClosedVerification) {
        this.lineToSend = lineToSend;
        this.onClosedVerification = onClosedVerification;
    }
    
    public BlockingSocketAdapter get(Socket socket) {
        return new SendLineSocketAdapter() {
            @Override
            protected void onReady() {
                sendLine(lineToSend);
                closeSocket();
            }
            
            @Override
            protected void onClosed() {
                waitForNewInput();
                if (onClosedVerification != null) {
                    onClosedVerification.accept(getServerSentString());
                }
            }
        };
    };
}
