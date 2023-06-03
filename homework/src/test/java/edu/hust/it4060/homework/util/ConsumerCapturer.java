package edu.hust.it4060.homework.util;

import java.util.function.Consumer;

public class ConsumerCapturer<T> implements Consumer<T> {
    private T t;
    
    public T getCaptured() {
        return t;
    }
    
    @Override
    public void accept(T t) {
        this.t = t;
    }
    
}
