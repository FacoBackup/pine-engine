package com.pine;

import org.apache.commons.io.input.TailerListenerAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class LogListener extends TailerListenerAdapter {
    public enum LogLevel {
        ERROR,
        WARN,
        INFO;

        public static LogLevel of(String line) {
            if (line.contains("ERROR"))
                return ERROR;
            if (line.contains("WARN"))
                return WARN;
            return INFO;
        }
    }

    public record LogEntry(String line, LogLevel level) {
    }

    private static final List<LogEntry> logBuffer = new LinkedList<>();
    private static final int MAX_BUFFER_SIZE = 2000;
    private static int filledMessages = 0;
    private static final LogEntry[] readable = new LogEntry[MAX_BUFFER_SIZE];

    @Override
    public void handle(String line) {
        if (logBuffer.size() >= MAX_BUFFER_SIZE) {
            logBuffer.removeFirst();
            filledMessages--;
        }
        logBuffer.add(new LogEntry(line, LogLevel.of(line)));
        logBuffer.toArray(readable);
        filledMessages++;
    }

    public static LogEntry[] getLogMessages() {
        return readable;
    }

    public static int getFilledMessages() {
        return filledMessages;
    }
}
