package com.pine.panels.console;

import com.pine.core.view.AbstractView;
import com.pine.theme.Icons;
import com.pine.util.InMemoryAppender;
import imgui.ImGui;
import imgui.type.ImString;

public class ConsoleHeaderPanel extends AbstractView {
    private final ImString searchValue;

    public ConsoleHeaderPanel(ImString searchValue) {
        this.searchValue = searchValue;
    }

    @Override
    public void render() {
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
