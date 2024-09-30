package com.pine.panels.console;

import com.pine.Message;
import com.pine.MessageRepository;
import com.pine.MessageSeverity;
import com.pine.PInject;
import com.pine.dock.AbstractDockPanel;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImString;

import java.util.List;

public class ConsolePanel extends AbstractDockPanel {
    private static final ImVec4 ERROR = new ImVec4(1, 0, 0, 1);
    private static final ImVec4 WARN = new ImVec4(1, 1, 0, 1);
    private static final ImVec4 INFO = new ImVec4(0, 0, 1, 1);
    private static final int FLAGS = ImGuiTableFlags.ScrollY | ImGuiTableFlags.Resizable | ImGuiTableFlags.RowBg | ImGuiTableFlags.NoBordersInBody;

    @PInject
    public MessageRepository messageRepository;

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
            ImGui.tableSetupColumn("Severity", ImGuiTableColumnFlags.WidthFixed, 100f);
            ImGui.tableSetupColumn("Date", ImGuiTableColumnFlags.WidthFixed, 100f);
            ImGui.tableSetupColumn("Message", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableHeadersRow();

            List<Message> messagesHistory = messageRepository.getMessagesHistory();
            for (Message log : messagesHistory) {
                if (hasSearchValue && !log.message().contains(searchValue.get())) {
                    continue;
                }
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                if (log.severity() == MessageSeverity.ERROR) {
                    ImGui.textColored(ERROR, "Error");
                } else if (log.severity() == MessageSeverity.WARN) {
                    ImGui.textColored(WARN, "Warning");
                } else {
                    ImGui.textColored(INFO, "Info");
                }
                ImGui.tableNextColumn();
                ImGui.text(log.dateString());
                ImGui.tableNextColumn();
                ImGui.text(log.message());
            }
        }
        ImGui.endTable();


    }
}
