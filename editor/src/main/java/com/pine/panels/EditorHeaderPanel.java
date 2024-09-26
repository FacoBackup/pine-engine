package com.pine.panels;

import com.pine.theme.Icon;
import com.pine.PInject;
import com.pine.theme.ThemeRepository;
import com.pine.view.AbstractView;
import imgui.ImGui;

public class EditorHeaderPanel extends AbstractView {
    @PInject
    public ThemeRepository themeRepository;

    private static final int BUTTON_SIZE = 23;

    @Override
    public void renderInternal() {
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("New")) {

            }
            if (ImGui.menuItem("Open")) {

            }
            if (ImGui.menuItem("Exit")) {

            }
            ImGui.endMenu();
        }

        if (ImGui.button(Icon.SAVE.codePoint, BUTTON_SIZE, BUTTON_SIZE)) {

        }


        if (ImGui.button(Icon.UNDO.codePoint, BUTTON_SIZE, BUTTON_SIZE)) { /* Action for Undo */ }
        if (ImGui.button(Icon.REDO.codePoint, BUTTON_SIZE, BUTTON_SIZE)) { /* Action for Redo */ }

        if (ImGui.button(themeRepository.isDarkMode ? Icon.MOON.codePoint : Icon.LIGHTBULB.codePoint, BUTTON_SIZE, BUTTON_SIZE)) {
            themeRepository.isDarkMode = !themeRepository.isDarkMode;
        }
    }
}
