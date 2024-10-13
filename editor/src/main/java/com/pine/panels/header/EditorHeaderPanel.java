package com.pine.panels.header;

import com.pine.core.WindowService;
import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import com.pine.service.ProjectService;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiKey;
import imgui.type.ImString;

import static com.pine.panels.header.ViewportHeaderPanel.largeSpacing;
import static com.pine.panels.header.ViewportHeaderPanel.spacing;
import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class EditorHeaderPanel extends AbstractView {
    @PInject
    public ProjectService projectService;

    @PInject
    public EditorRepository editorRepository;

    private ImGuiIO io;
    private final ImString pathToProject = new ImString();

    @Override
    public void onInitialize() {
        io = ImGui.getIO();
        appendChild(new ViewportHeaderPanel());
    }

    @Override
    public void render() {
        hotKeys();
        renderFileTab();
        ImGui.separator();
        super.render();
    }

    private void renderFileTab() {
        if (ImGui.button(Icons.create + "New##newProject")) {
            projectService.newProject();
        }
        ImGui.sameLine();

        if (ImGui.button(Icons.file_open + "Open##openProject")) {
            projectService.openProject();
        }
        ImGui.sameLine();

        spacing();
        if (ImGui.button(Icons.save + "Save##save")) {
            projectService.save();
        }
        ImGui.sameLine();

        spacing();

        if (ImGui.button(Icons.undo, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) { /* Action for Undo */ }
        ImGui.sameLine();

        if (ImGui.button(Icons.redo, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) { /* Action for Redo */ }
        ImGui.sameLine();

        pathToProject.set(projectService.getProjectDirectory());
        ImGui.sameLine();
        ImGui.dummy(10, 0);
        ImGui.sameLine();
        ImGui.dummy(ImGui.getContentRegionAvailX() - 300, 0);
        ImGui.sameLine();
        ImGui.setNextItemWidth(200);
        ImGui.inputText(imguiId + "input", pathToProject, ImGuiInputTextFlags.ReadOnly);
        ImGui.sameLine();
        framerate();
    }

    private void framerate() {
        int framerate = Math.max(1, (int) io.getFramerate());
        ImGui.text(1000 / framerate + "ms | " + framerate + "fps");
    }

    private void hotKeys(){
        if (ImGui.isKeyDown(ImGuiKey.LeftCtrl) && ImGui.isKeyPressed(ImGuiKey.S)) {
            projectService.save();
        }

        if (ImGui.isKeyDown(ImGuiKey.LeftCtrl) && ImGui.isKeyPressed(ImGuiKey.Z)) {
            // TODO
        }

        if (ImGui.isKeyDown(ImGuiKey.LeftCtrl) && ImGui.isKeyPressed(ImGuiKey.Y)) {
            // TODO
        }
    }
}
