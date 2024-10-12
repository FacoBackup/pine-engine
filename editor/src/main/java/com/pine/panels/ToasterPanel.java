package com.pine.panels;

import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.messaging.Message;
import com.pine.messaging.MessageRepository;
import com.pine.service.ThemeService;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

import static com.pine.messaging.MessageRepository.MESSAGE_DURATION;

public class ToasterPanel extends AbstractView {
    @PInject
    public MessageRepository messageRepository;

    @PInject
    public ThemeService themeService;

    @Override
    public void render() {
        Message[] messages = messageRepository.getMessages();
        int usedIndices = 0;
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
            ImGui.setNextWindowPos(5, viewportDimensions.y - 40 * (usedIndices + 1));
            ImGui.setNextWindowSize(ImGui.calcTextSizeX(message.message()) + 45, 35);
            ImGui.pushStyleColor(ImGuiCol.WindowBg, themeService.palette4);
            ImGui.begin("##toaster" + usedIndices, ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoSavedSettings);
            ImGui.popStyleColor();
            ImGui.textColored(message.severity().getColor(), Icons.warning);
            ImGui.sameLine();
            ImGui.text(message.message());
            ImGui.end();
            usedIndices++;
        }
    }
}
