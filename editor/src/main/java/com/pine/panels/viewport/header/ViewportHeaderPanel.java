package com.pine.panels.viewport.header;

import com.pine.core.AbstractView;
import com.pine.core.UIUtil;
import com.pine.injection.PInject;
import com.pine.repository.EditorMode;
import com.pine.repository.EditorRepository;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import static com.pine.core.dock.DockSpacePanel.FRAME_SIZE;

public class ViewportHeaderPanel extends AbstractView {
    private static final int FLAGS = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse;
    private static final ImVec2 PADDING = new ImVec2(4, 4);
    private static final float HEIGHT = 35;

    @PInject
    public EditorRepository editorRepository;

    private AbstractViewportSettingsPanel paintGizmo;
    private AbstractViewportSettingsPanel gizmo;

    @Override
    public void onInitialize() {
        appendChild(gizmo = new GizmoSettingsPanel());
        appendChild(paintGizmo = new PaintingSettingsPanel());
    }

    @Override
    public void render() {
        AbstractViewportSettingsPanel currentPanel;
        if (editorRepository.editorMode == EditorMode.TRANSFORM) {
            currentPanel = gizmo;
        } else {
            currentPanel = paintGizmo;
        }
        ImGui.setNextWindowPos(ImGui.getWindowPosX(), ImGui.getWindowPosY() + FRAME_SIZE);
        ImGui.setNextWindowSize(ImGui.getWindowSizeX(), HEIGHT);
        ImGui.setNextWindowBgAlpha(.4f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, PADDING);
        ImGui.begin(imguiId, UIUtil.OPEN, FLAGS);
        ImGui.popStyleVar();
        currentPanel.render();
        ImGui.end();

        currentPanel.renderOutside();
    }

}
