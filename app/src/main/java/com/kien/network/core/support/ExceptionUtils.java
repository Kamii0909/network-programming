package com.kien.network.core.support;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class ExceptionUtils {
    private ExceptionUtils() {
    }
    
    @FunctionalInterface
    public interface CheckedRunnable {
        @SuppressWarnings("java:S112")
        void run() throws Exception;
    }
    
    public static void unlikely(CheckedRunnable runnable, Consumer<Exception> ifFail) {
        try {
            runnable.run();
        } catch (Exception e) {
            ifFail.accept(e);
        }
    }
    
    @SuppressWarnings("java:S112")
    public static <T> T unlikely(Callable<T> callable) throws RuntimeException {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
