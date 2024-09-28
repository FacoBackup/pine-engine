package com.pine.repository;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Message {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
    private final String message;
    private final MessageSeverity severity;
    private final Date displayStartTime = new Date();
    private final String messageWithTime;

    public Message(String message, MessageSeverity severity) {
        this.messageWithTime = formatter.format(displayStartTime) + " - " + message;
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

    public String messageWithTime() {
        return messageWithTime;
    }
}
