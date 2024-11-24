package com.pine.editor.panels.header;

import com.pine.editor.core.AbstractView;
import com.pine.editor.core.UIUtil;
import com.pine.common.injection.PInject;
import com.pine.editor.repository.EditorRepository;
import com.pine.editor.service.ProjectService;
import com.pine.common.Icons;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiKey;
import imgui.type.ImString;

import static com.pine.common.Icons.ONLY_ICON_BUTTON_SIZE;

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
        appendChild(new GlobalSettingsPanel());
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

        UIUtil.spacing();
        if (ImGui.button(Icons.save + "Save##save")) {
            projectService.save();
        }
        ImGui.sameLine();
        UIUtil.spacing();

        if (ImGui.button(Icons.fullscreen + "##fullscreen", ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            editorRepository.fullScreen = true;
        }
        ImGui.sameLine();
        UIUtil.spacing();

        if (ImGui.button(Icons.undo, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) { /* Action for Undo */ }
        ImGui.sameLine();

        if (ImGui.button(Icons.redo, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) { /* Action for Redo */ }
        ImGui.sameLine();

        pathToProject.set(projectService.getProjectDirectory());
        ImGui.sameLine();
        UIUtil.dynamicSpacing(300);

        ImGui.setNextItemWidth(200);
        ImGui.inputText(imguiId + "input", pathToProject, ImGuiInputTextFlags.ReadOnly);
        ImGui.sameLine();
        framerate();
    }

    private void framerate() {
        int framerate = Math.max(1, (int) io.getFramerate());
        ImGui.text(1000 / framerate + "ms | " + framerate + "fps");
    }

    private void hotKeys() {
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
