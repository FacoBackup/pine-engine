package com.pine.panels;

import com.pine.PInject;
import com.pine.theme.Icons;
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

        if (ImGui.button(Icons.save, BUTTON_SIZE, BUTTON_SIZE)) {

        }


        if (ImGui.button(Icons.undo, BUTTON_SIZE, BUTTON_SIZE)) { /* Action for Undo */ }
        if (ImGui.button(Icons.redo, BUTTON_SIZE, BUTTON_SIZE)) { /* Action for Redo */ }

        if (ImGui.button(themeRepository.isDarkMode ? Icons.dark_mode : Icons.light_mode, BUTTON_SIZE, BUTTON_SIZE)) {
            themeRepository.isDarkMode = !themeRepository.isDarkMode;
        }
    }
}
