package com.pine.panels.console;

import com.pine.theme.Icons;
import com.pine.util.InMemoryAppender;
import com.pine.view.AbstractView;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

public class ConsoleHeaderPanel extends AbstractView {
    private final ImString searchValue;

    public ConsoleHeaderPanel(ImString searchValue) {
        this.searchValue = searchValue;
    }

    @Override
    public void renderInternal() {
        if (ImGui.button(Icons.clear_all + " Clear console##clearConsole")) {
            InMemoryAppender.clearMessagesHistory();
        }
        ImGui.sameLine();
        ImGui.spacing();
        ImGui.sameLine();
        ImGui.text(Icons.search);
        ImGui.sameLine();
        ImGui.inputText("##searchMessage", searchValue);
    }
}
