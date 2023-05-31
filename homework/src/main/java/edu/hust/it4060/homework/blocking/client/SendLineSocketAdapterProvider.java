package edu.hust.it4060.homework.blocking.client;

import java.net.Socket;

import com.kien.network.core.socket.api.adapter.BlockingSocketAdapter;
import com.kien.network.core.socket.api.adapter.BlockingSocketAdapterProvider;

public class SendLineSocketAdapterProvider implements BlockingSocketAdapterProvider {
    
    @Override
    public BlockingSocketAdapter get(Socket context) {
        return new SendLineSocketAdapter();
    }
    
}
