package com.pine.panels.console;

import com.pine.PInject;
import com.pine.dock.AbstractDockPanel;
import com.pine.MessageRepository;
import com.pine.MessageSeverity;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.type.ImString;

public class ConsolePanel extends AbstractDockPanel {
    private static final ImVec4 ERROR = new ImVec4(0.35f, 0, 0, 1);
    private static final ImVec4 WARN = new ImVec4(1, 0.59f, 0, 1);
    private static final ImVec4 INFO = new ImVec4(0, .5f, 0, 1);

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
        for(int i = 0; i < messageRepository.getMessagesHistory().size(); i++){
            var log = messageRepository.getMessagesHistory().get(i);
            if(hasSearchValue && !log.messageWithTime().contains(searchValue.get())){
                continue;
            }
            if (log.severity() == MessageSeverity.ERROR) {
                ImGui.textColored(ERROR, log.messageWithTime());
            } else if (log.severity() == MessageSeverity.WARN) {
                ImGui.textColored(WARN, log.messageWithTime());
            } else {
                ImGui.textColored(INFO, log.messageWithTime());
            }
        }
    }
}
