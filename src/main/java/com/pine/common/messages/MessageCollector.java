package com.pine.common.messages;

/**
 * Intended for grouping messages system-wide
 */
public class MessageCollector {
    public static final int MAX_MESSAGES = 4;
    public static final long MESSAGE_DURATION = 3000;
    private static final Message[] messages = new Message[MAX_MESSAGES];

    public static void pushMessage(String message, MessageSeverity severity) {
        if (messages[0] == null) {
            messages[0] = new Message(message, severity);
        } else if (messages[1] == null) {
            messages[1] = new Message(message, severity);
        } else if (messages[2] == null) {
            messages[2] = new Message(message, severity);
        } else {
            messages[3] = new Message(message, severity);
        }
    }

    public static Message[] getMessages() {
        return messages;
    }
}
