package com.jengine.app.view.core.window;

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
