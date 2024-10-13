package com.pine.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogMessage {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("MM-dd HH:mm");

    public final String message;
    public final Level level;
    public final String date;

    public LogMessage(LogEvent event) {
        this.message = event.getMessage().getFormattedMessage();
        this.level = event.getLevel();
        this.date = FORMATTER.format(new Date(event.getTimeMillis()));
    }

    public LogMessage(String message, long time) {
        this.level = Level.ERROR;
        this.message = message;
        this.date = FORMATTER.format(new Date(time));
    }
}
