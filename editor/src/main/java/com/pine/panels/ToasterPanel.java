package com.pine.panels;

import com.pine.messaging.Message;
import com.pine.messaging.MessageRepository;
import com.pine.injection.PInject;
import com.pine.view.AbstractView;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

import static com.pine.messaging.MessageRepository.MESSAGE_DURATION;

public class ToasterPanel extends AbstractView {
    @PInject
    public MessageRepository messageRepository;

    @Override
    public void renderInternal() {
        Message[] messages = messageRepository.getMessages();
        for (int i = 0; i < MessageRepository.MAX_MESSAGES; i++) {
            var message = messages[i];
            if (message == null) {
                continue;
            }
            if (System.currentTimeMillis() - message.getDisplayStartTime().getTime() > MESSAGE_DURATION) {
                messages[i] = null;
                continue;
            }
            ImVec2 viewportDimensions = ImGui.getMainViewport().getSize();
            ImGui.setNextWindowPos(5, viewportDimensions.y - 40 * (i + 1));
            ImGui.setNextWindowSize(viewportDimensions.x * .35F, 35);
            ImGui.pushStyleColor(ImGuiCol.Border, message.severity().getColor());
            ImGui.pushStyleColor(ImGuiCol.WindowBg, message.severity().getColor());
            ImGui.begin("##toaster" + i, ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoSavedSettings);
            ImGui.popStyleColor();
            ImGui.popStyleColor();
            ImGui.text(message.message());
            ImGui.end();
        }
    }
}
