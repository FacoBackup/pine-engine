package com.pine.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Plugin(name = "InMemoryAppender", category = "Core", elementType = "appender", printObject = true)
public class InMemoryAppender extends AbstractAppender {
    public static final int MAX_MESSAGES_HISTORY = 100;

    private static final List<LogMessage> logMessagesSource = new LinkedList<>();
    private static final List<LogMessage> copy = new LinkedList<>();
    private static boolean isSynced = false;

    protected InMemoryAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    public static void clearMessagesHistory() {
        logMessagesSource.clear();
        copy.clear();
    }

    @Override
    public void append(LogEvent event) {
        if (logMessagesSource.size() >= MAX_MESSAGES_HISTORY) {
            logMessagesSource.removeFirst();
        }
        if (event.getLevel() == Level.ERROR && event.getThrown() != null) {
            logMessagesSource.add(new LogMessage(event.getThrown().getMessage(), event.getTimeMillis()));
        } else {
            logMessagesSource.add(new LogMessage(event));
        }
        isSynced = false;
    }

    public static List<LogMessage> getLogMessages() {
        return copy;
    }

    public static void sync() {
        if (!isSynced) {
            try{
                copy.clear();
                copy.addAll(logMessagesSource);
            }catch (Exception ignored){}
        }
        isSynced = true;
    }

    @PluginFactory
    public static InMemoryAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") Filter filter) {

        if (name == null) {
            return null;
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new InMemoryAppender(name, filter, layout, true);
    }
}
