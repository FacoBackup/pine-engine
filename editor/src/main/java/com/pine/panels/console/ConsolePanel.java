package com.pine.panels.console;

import com.pine.core.dock.AbstractDockPanel;
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
    public static final int TABLE_FLAGS = ImGuiTableFlags.ScrollY | ImGuiTableFlags.Resizable | ImGuiTableFlags.RowBg | ImGuiTableFlags.NoBordersInBody;

    private final ImString searchValue = new ImString();

    @Override
    public void onInitialize() {
        appendChild(new ConsoleHeaderPanel(searchValue));
    }

    @Override
    public void render() {
        super.render();
        boolean hasSearchValue = !searchValue.isEmpty();

        if (ImGui.beginTable("##console" + imguiId, 3, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("Date", ImGuiTableColumnFlags.WidthFixed, 120f);
            ImGui.tableSetupColumn("Severity", ImGuiTableColumnFlags.WidthFixed, 80f);
            ImGui.tableSetupColumn("Message", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableHeadersRow();

            List<LogMessage> logMessages = InMemoryAppender.getLogMessages();
            for (LogMessage log : logMessages) {
                if (log == null || (hasSearchValue && !log.message.contains(searchValue.get()))) {
                    continue;
                }
                ImGui.tableNextRow();

                ImGui.tableNextColumn();
                ImGui.text(log.date);

                ImGui.tableNextColumn();
                if (log.level == Level.ERROR) {
                    ImGui.textColored(ERROR, "Error");
                } else {
                    ImGui.text("Info");
                }

                ImGui.tableNextColumn();
                ImGui.text(log.message);
            }
            InMemoryAppender.sync();
            ImGui.endTable();
        }
    }
}
