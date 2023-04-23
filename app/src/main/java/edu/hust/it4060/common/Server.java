package edu.hust.it4060.common;

/**
 * A Server is a Lifecycle manager, it is responsible for invoking lifecycle and
 * event hooks.
 */
public interface Server extends AutoCloseable, LifeCycle {
    void addLifeCycle(LifeCycle lifeCycle);
    
    LifeCycle unmanageLifeCycle(LifeCycle lifeCycle);
}
