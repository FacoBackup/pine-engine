package com.pine.messaging;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Message implements Serializable {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String message;
    private final MessageSeverity severity;
    private final Date displayStartTime = new Date();
    private final String dateString;

    public Message(String message, MessageSeverity severity) {
        this.dateString = formatter.format(displayStartTime);
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

    public String dateString() {
        return dateString;
    }
}
