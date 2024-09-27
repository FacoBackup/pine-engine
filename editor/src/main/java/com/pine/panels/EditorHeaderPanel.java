package com.pine.panels;

import com.pine.PInject;
import com.pine.theme.Icons;
import com.pine.repository.ThemeService;
import com.pine.view.AbstractView;
import imgui.ImGui;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class EditorHeaderPanel extends AbstractView {
    @PInject
    public ThemeService themeService;


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

        if (ImGui.button(Icons.save, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {

        }

        if (ImGui.button(Icons.undo, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) { /* Action for Undo */ }
        if (ImGui.button(Icons.redo, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) { /* Action for Redo */ }

        if (ImGui.button(themeService.isDarkMode ? Icons.dark_mode : Icons.light_mode, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            themeService.isDarkMode = !themeService.isDarkMode;
        }
    }
}
