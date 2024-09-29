package com.pine.panels;

import com.pine.PInject;
import com.pine.PineEngine;
import com.pine.repository.ThemeService;
import com.pine.service.ProjectService;
import com.pine.theme.Icons;
import com.pine.view.AbstractView;
import imgui.ImGui;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class EditorHeaderPanel extends AbstractView {
    @PInject
    public ThemeService themeService;

    @PInject
    public ProjectService projectService;

    @Override
    public void renderInternal() {
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("New")) {
                projectService.newProject();
            }
            if (ImGui.menuItem("Open")) {
                projectService.openProject();
            }
            if (ImGui.menuItem("Save & exit")) {
                projectService.save();
                PineEngine.shouldStop = true;
            }
            ImGui.endMenu();
        }

        if (ImGui.button(Icons.save, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            projectService.save();
        }

        if (ImGui.button(Icons.undo, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) { /* Action for Undo */ }
        if (ImGui.button(Icons.redo, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) { /* Action for Redo */ }
    }
}
