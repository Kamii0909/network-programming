package com.kien.network.core.socket.internal.context;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import com.kien.network.core.socket.api.adapter.SocketAdapter;
import com.kien.network.core.socket.api.context.SocketContext;

public abstract sealed class AbstractSocketContext<S, A extends SocketAdapter<S>> implements SocketContext<S>
    permits DefaultBlockingSocketContext, DefaultSocketChannelContext {
    protected final S socket;
    protected final A socketAdapter;
    private Object attachment;
    private ReentrantLock attachmentLock = new ReentrantLock();
    
    protected AbstractSocketContext(S socket, A socketAdapter) {
        this.socket = socket;
        this.socketAdapter = socketAdapter;
    }
    
    @Override
    public S getSocket() {
        return socket;
    }
    
    @Override
    public A getSocketAdapter() {
        return socketAdapter;
    }

    @Override
    public Object attach(Object newVal) {
        try {
            attachmentLock.lock();
            Object oldVal = attachment;
            attachment = newVal;
            return oldVal;
        } finally {
            attachmentLock.unlock();
        }
    }
    
    @Override
    public Object getAttachment() {
        try {
            attachmentLock.lock();
            return attachment;
        } finally {
            attachmentLock.unlock();
        }
    }
    
    @Override
    public void ifAttachmentPresent(Consumer<Object> action) {
        try {
            attachmentLock.lock();
            if (attachment == null) {
                return;
            }
            if (action == null) {
                throw new IllegalArgumentException("Action is null");
            }
            action.accept(attachment);
        } finally {
            attachmentLock.unlock();
        }
    }
}
