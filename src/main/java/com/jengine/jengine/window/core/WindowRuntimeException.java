package com.jengine.jengine.window.core;

public class WindowRuntimeException extends Exception {
    public WindowRuntimeException(String message) {
        super(message);
    }

    public WindowRuntimeException(Exception e) {
        super(e);
    }

    public static WindowRuntimeException rethrow(Exception e) {
        return new WindowRuntimeException(e);
    }
}
