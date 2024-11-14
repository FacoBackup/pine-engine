package com.pine.panels.viewport;

import com.pine.core.AbstractView;
import com.pine.core.UIUtil;
import com.pine.injection.PInject;
import com.pine.repository.*;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.ref.TextureResourceRef;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import static com.pine.core.AbstractWindow.*;
import static com.pine.core.dock.DockSpacePanel.FRAME_SIZE;
import static com.pine.panels.viewport.ViewportPanel.INV_X;
import static com.pine.panels.viewport.ViewportPanel.INV_Y;
import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

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
