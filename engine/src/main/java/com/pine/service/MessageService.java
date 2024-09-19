package com.pine.service;

import com.pine.injection.EngineInjectable;

import java.util.function.BiConsumer;

@EngineInjectable
public class MessageService {
    private BiConsumer<String, Boolean> messageCallback;

    public void setMessageCallback(BiConsumer<String, Boolean> messageCallback) {
        this.messageCallback = messageCallback;
    }

    public void onMessage(String message, boolean isError){
        this.messageCallback.accept(message, isError);
    }
}
