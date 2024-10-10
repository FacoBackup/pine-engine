package com.pine.panels.console;

import com.pine.dock.AbstractDockPanel;
import com.pine.util.InMemoryAppender;
import com.pine.util.LogMessage;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImString;
import org.apache.logging.log4j.Level;

import java.util.List;

public class ConsolePanel extends AbstractDockPanel {
    private static final ImVec4 ERROR = new ImVec4(1, 0, 0, 1);
    private static final ImVec4 WARN = new ImVec4(1, 1, 0, 1);
    private static final ImVec4 INFO = new ImVec4(0, .5f, 1, 1);
    private static final int FLAGS = ImGuiTableFlags.ScrollY | ImGuiTableFlags.Resizable | ImGuiTableFlags.RowBg | ImGuiTableFlags.NoBordersInBody;

    private final ImString searchValue = new ImString();

    @Override
    public void onInitialize() {
        appendChild(new ConsoleHeaderPanel(searchValue));
    }

    @Override
    public void renderInternal() {
        super.renderInternal();
        boolean hasSearchValue = !searchValue.isEmpty();

        if (ImGui.beginTable("##console" + imguiId, 3, FLAGS)) {
            ImGui.tableSetupColumn("Date", ImGuiTableColumnFlags.WidthFixed, 120f);
            ImGui.tableSetupColumn("Severity", ImGuiTableColumnFlags.WidthFixed, 80f);
            ImGui.tableSetupColumn("Message", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableHeadersRow();

            List<LogMessage> logMessages = InMemoryAppender.getLogMessages();
            for (int i = 0, messagesHistorySize = logMessages.size(); i < messagesHistorySize; i++) {
                LogMessage log = logMessages.get(i);
                if (hasSearchValue && !log.message.contains(searchValue.get())) {
                    continue;
                }
                ImGui.tableNextRow();

                ImGui.tableNextColumn();
                ImGui.text(log.date);

                ImGui.tableNextColumn();
                if (log.level == Level.ERROR) {
                    ImGui.textColored(ERROR, "Error");
                } else if (log.level == Level.WARN) {
                    ImGui.textColored(WARN, "Warning");
                } else {
                    ImGui.textColored(INFO, "Info");
                }

                ImGui.tableNextColumn();
                ImGui.text(log.message);
            }
            InMemoryAppender.sync();
            ImGui.endTable();
        }
    }
}
