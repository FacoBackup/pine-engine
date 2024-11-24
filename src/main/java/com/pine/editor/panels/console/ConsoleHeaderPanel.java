package com.pine.editor.panels.console;

import com.pine.editor.core.AbstractView;
import com.pine.common.Icons;
import com.pine.editor.util.InMemoryAppender;
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
