package edu.hust.it4060.homework.blocking.server;

import com.kien.network.core.support.adapter.AbstractLineBasedBlockingSocketAdapter;

class HelloWorldHttpSocketAdapter extends AbstractLineBasedBlockingSocketAdapter {
    
    @Override
    protected void onReady() {
        sendMultiLineString("""
               HTTP/1.1 200 OK
               Content-Type: text/html
               
               <html><body><h1>Xin chao cac ban</h1></body></html>
            """);
    }
    
    @Override
    protected void newLineRead(String newLine) {
        // Do nothing
    }
}
