package com.pine.service;

import com.pine.PBean;

import java.util.function.BiConsumer;

@PBean
public class MessageService {
    private BiConsumer<String, Boolean> messageCallback;

    public void setMessageCallback(BiConsumer<String, Boolean> messageCallback) {
        this.messageCallback = messageCallback;
    }

    public void onMessage(String message, boolean isError){
        this.messageCallback.accept(message, isError);
    }
}
