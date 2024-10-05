package com.pine.panels;

import com.pine.WindowService;
import com.pine.dock.AbstractDockHeader;
import com.pine.injection.PInject;
import com.pine.service.ProjectService;
import com.pine.service.ThemeService;
import com.pine.theme.Icons;
import com.pine.view.AbstractView;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class EditorHeaderPanel extends AbstractDockHeader {
    @PInject
    public WindowService windowService;

    @PInject
    public ProjectService projectService;

    private ImGuiIO io;
    private final ImString pathToProject = new ImString();

    @Override
    public void onInitialize() {
        io = ImGui.getIO();
    }

    @Override
    public void begin() {
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("New")) {
                projectService.newProject();
            }
            if (ImGui.menuItem("Open")) {
                projectService.openProject();
            }
            if (ImGui.menuItem("Save & exit")) {
                projectService.save();
                windowService.stop();
            }
            ImGui.endMenu();
        }

        if (ImGui.button(Icons.save, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            projectService.save();
        }

        if (ImGui.button(Icons.undo, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) { /* Action for Undo */ }
        if (ImGui.button(Icons.redo, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) { /* Action for Redo */ }

    }

    @Override
    public void end() {
        pathToProject.set(projectService.getProjectDirectory());
        ImGui.sameLine();
        ImGui.dummy(ImGui.getContentRegionAvailX() - 302, 0);
        ImGui.sameLine();
        framerate();
        ImGui.sameLine();
        ImGui.dummy(10, 0);
        ImGui.sameLine();
        ImGui.setNextItemWidth(200);
        ImGui.inputText("", pathToProject, ImGuiInputTextFlags.ReadOnly);
    }

    private void framerate() {
        int framerate = Math.max(1, (int) io.getFramerate());
        ImGui.text(1000 / framerate + "ms | " + framerate + "fps");
    }

}
