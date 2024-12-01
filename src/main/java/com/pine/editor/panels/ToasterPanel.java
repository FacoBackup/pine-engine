package com.pine.editor.panels;

import com.pine.common.injection.PInject;
import com.pine.common.messaging.Message;
import com.pine.common.messaging.MessageRepository;
import com.pine.editor.core.AbstractView;
import com.pine.editor.service.ThemeService;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

import static com.pine.common.messaging.MessageRepository.MESSAGE_DURATION;
import static com.pine.editor.core.UIUtil.OPEN;

public class ToasterPanel extends AbstractView {
    private static final int FLAGS = ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoSavedSettings;

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

            ImGui.begin("##toaster" + usedIndices, OPEN, FLAGS);
            ImGui.popStyleColor();
            ImGui.textColored(message.severity().getColor(), message.severity().getIcon());
            ImGui.sameLine();
            ImGui.text(message.message());
            ImGui.end();

            usedIndices++;
        }
    }
}
