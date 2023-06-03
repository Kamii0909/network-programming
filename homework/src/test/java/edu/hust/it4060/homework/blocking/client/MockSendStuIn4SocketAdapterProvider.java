package edu.hust.it4060.homework.blocking.client;

import java.net.Socket;

import com.kien.network.core.socket.api.adapter.BlockingSocketAdapter;
import com.kien.network.core.socket.api.adapter.BlockingSocketAdapterProvider;

public class MockSendStuIn4SocketAdapterProvider implements BlockingSocketAdapterProvider {

    public MockSendStuIn4SocketAdapterProvider() {
    }
    
    public BlockingSocketAdapter get(Socket socket) {
        return new SendStuIn4SocketAdapter() {
            @Override
            protected void onReady() {
                sendLine("20190078 Ha Trung Kien 2001-08-09 0.0");
                closeSocket();
            }

            @Override
            protected void onClosed() {
                System.out.println("Closing socket");
            }
        };
    }
}
