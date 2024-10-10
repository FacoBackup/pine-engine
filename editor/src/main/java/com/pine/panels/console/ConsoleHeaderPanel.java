package com.pine.panels.console;

import com.pine.messaging.MessageRepository;
import com.pine.injection.PInject;
import com.pine.theme.Icons;
import com.pine.view.AbstractView;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

public class ConsoleHeaderPanel extends AbstractView {
    private final ImString searchValue;
    @PInject
    public MessageRepository messageRepository;

    public ConsoleHeaderPanel(ImString searchValue) {
        this.searchValue = searchValue;
    }

    @Override
    public void renderInternal() {
        if (ImGui.button(Icons.clear_all + " Clear console##clearConsole")) {
            messageRepository.clearMessagesHistory();
        }
        ImGui.sameLine();
        ImGui.spacing();
        ImGui.sameLine();
        ImGui.text(Icons.search);
        ImGui.sameLine();
        ImGui.inputText("##searchMessage", searchValue, ImGuiInputTextFlags.EnterReturnsTrue);
    }
}
