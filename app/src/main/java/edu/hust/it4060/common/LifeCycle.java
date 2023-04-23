package edu.hust.it4060.common;

import java.util.EventListener;

/**
 * A Lifecycle object is managed by the Server.
 */
public interface LifeCycle {
    void start();
    
    void stop();
    
    boolean isRunning();
    
    boolean isStopped();
    
    boolean addEventListener(EventListener eventListener);
    
    boolean removeEventListener(EventListener eventListener);
}
