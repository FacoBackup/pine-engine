package com.pine.service;

public final class Message {
    private final String message;
    private final MessageSeverity severity;
    private final long displayStartTime = System.currentTimeMillis();

    public Message(String message, MessageSeverity severity) {
        this.message = message;
        this.severity = severity;
    }

    public String message() {
        return message;
    }

    public MessageSeverity severity() {
        return severity;
    }

    public long getDisplayStartTime() {
        return displayStartTime;
    }
}
