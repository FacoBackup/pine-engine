package com.pine.panels.console;

import com.pine.LogListener;
import com.pine.dock.AbstractDockPanel;
import imgui.ImGui;
import imgui.ImVec4;

import static com.pine.LogListener.getLogMessages;

public class ConsolePanel extends AbstractDockPanel {
    private static final ImVec4 ERROR = new ImVec4(0.35f, 0, 0, 1);
    private static final ImVec4 WARN = new ImVec4(1, 0.59f, 0, 1);
    private static final ImVec4 INFO = new ImVec4(0, .5f, 0, 1);

    @Override
    public void renderInternal() {
        float max = size.y / ImGui.getTextLineHeight();
        LogListener.LogEntry[] messages = getLogMessages();
        for(int i = 0; i < Math.min(max, LogListener.getFilledMessages()); i++){
            var log = messages[i];
            if(log == null){
                break;
            }
            if (log.level() == LogListener.LogLevel.ERROR) {
                ImGui.textColored(ERROR, log.line());
            } else if (log.level() == LogListener.LogLevel.WARN) {
                ImGui.textColored(WARN, log.line());
            } else {
                ImGui.textColored(INFO, log.line());
            }
        }
    }
}
