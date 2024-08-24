package com.pine.app;

public class ResourceRuntimeException extends Exception {
    public ResourceRuntimeException(String message) {
        super(message);
    }

    public ResourceRuntimeException(Exception e) {
        super(e);
    }

    public static ResourceRuntimeException rethrow(Exception e) {
        return new ResourceRuntimeException(e);
    }
}
