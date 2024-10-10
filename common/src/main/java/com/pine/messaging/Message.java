package com.pine.messaging;

import java.io.Serializable;
import java.util.Date;

public final class Message implements Serializable {
    private final String message;
    private final MessageSeverity severity;
    private final Date displayStartTime = new Date();

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

    public Date getDisplayStartTime() {
        return displayStartTime;
    }
}
