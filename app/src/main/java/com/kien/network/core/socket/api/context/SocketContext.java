package com.kien.network.core.socket.api.context;

import java.util.function.Consumer;

import com.kien.network.core.socket.api.adapter.SocketAdapter;
import com.kien.network.core.socket.internal.context.AbstractSocketContext;

/**
 * The SocketContext class provide various transport mechanism independant
 * interaction with the underlying socket. The socket context is valid for the
 * whole processing of a single socket.
 * <p>
 * Implementation is not required to be thread safe.
 * 
 * @param T the Socket type
 */
public sealed interface SocketContext<T>
    permits AbstractSocketContext<T, ? extends SocketAdapter<T>>, BlockingSocketContext, SocketChannelContext {
    
    /**
     * Get the underlying transportation mechanism.
     * 
     * @return the socket object
     */
    T getSocket();
    
    /**
     * @return the socket adapter configured for this socket.
     */
    SocketAdapter<T> getSocketAdapter();
    
    /**
     * Write to an internal buffer. This will not flush the stream, to make data
     * transferred between socket, use {@link #flush()}. Implementation may reject
     * this call if the argument is not writable to the socket. This method will not
     * block.
     * 
     * @param data typically a byte[] or ByteBuffer
     * @implNote If it is a ByteBuffer, remember to call flip beforehand.
     */
    void write(Object data);
    
    /**
     * Flush the stream, ensure data written through the socket. It is
     * implementation dependant whether this will block.
     */
    void flush();
    
    /**
     * Request a read operation from the underlying socket, triggering
     * {@link SocketAdapter#onRead(SocketContext, byte[])} to be called. This method
     * blocks until {@link SocketAdapter#onRead(SocketContext, byte[]) onRead}
     * returns if any data was read. Otherwise this method returns immediately as a
     * noop, effectively making this call implicitly non blocking.
     */
    void read();
    
    /**
     * Close the underlying socket.
     */
    void close();
    
    /**
     * Attach an object to the context. Replace the old value.
     * 
     * @param o the new value
     * @return the old value, or null if none.
     */
    Object attach(Object o);
    
    /**
     * Get the attachment if any. If there is no attachment, this method return
     * null.
     */
    Object getAttachment();
    
    /**
     * Action to be invoked if there is any attachment present.
     */
    void ifAttachmentPresent(Consumer<Object> action);
}
