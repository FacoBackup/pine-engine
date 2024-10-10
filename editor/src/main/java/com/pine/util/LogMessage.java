package com.pine.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogMessage {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public final String message;
    public final Level level;
    public final String date;

    public LogMessage(LogEvent event) {
        this.message = event.getMessage().getFormattedMessage();
        this.level = event.getLevel();
        this.date = FORMATTER.format(new Date(event.getTimeMillis()));
    }
}
