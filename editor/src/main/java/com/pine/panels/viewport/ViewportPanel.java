package com.pine.panels.viewport;

import com.pine.Engine;
import com.pine.core.AbstractView;
import com.pine.injection.PInject;
import com.pine.panels.AbstractEntityViewPanel;
import com.pine.panels.viewport.header.ViewportHeaderPanel;
import com.pine.repository.CameraRepository;
import com.pine.repository.EditorMode;
import com.pine.repository.EditorRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.service.ViewportPickingService;
import com.pine.service.camera.Camera;
import com.pine.service.camera.CameraMovementService;
import com.pine.service.resource.fbo.FrameBufferObject;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseButton;

import static com.pine.core.dock.DockSpacePanel.FRAME_SIZE;

public class ViewportPanel extends AbstractViewportPanel {

    public static final ImVec2 INV_X = new ImVec2(1, 0);
    public static final ImVec2 INV_Y = new ImVec2(0, 1);

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public ViewportPickingService viewportPickingService;

    private AbstractView headerPanel;
    private AbstractView cameraPanel;
    private GizmoPanel gizmoPanel;

    @Override
    public void onInitialize() {
        super.onInitialize();
        appendChild(headerPanel = new ViewportHeaderPanel());
        appendChild(gizmoPanel = new GizmoPanel(position, sizeVec));
        appendChild(cameraPanel = new CameraPositionPanel());
    }

    @Override
    public void render() {
        updateCamera();
        hotKeys();
        tick();

        renderFrame();

        if (editorRepository.editorMode == EditorMode.TRANSFORM) {
            gizmoPanel.render();
        }
        headerPanel.render();
        cameraPanel.render();
    }

    @Override
    protected void hotKeysInternal() {
        if (ImGui.isKeyPressed(ImGuiKey.T))
            editorRepository.gizmoType = Operation.TRANSLATE;
        if (ImGui.isKeyPressed(ImGuiKey.R))
            editorRepository.gizmoType = Operation.ROTATE;
        if (ImGui.isKeyPressed(ImGuiKey.Y))
            editorRepository.gizmoType = Operation.SCALE;

        if (editorRepository.editorMode == EditorMode.TRANSFORM && !gizmoPanel.isGizmoOver && ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
            viewportPickingService.pick();
        }
    }

    @Override
    protected String getCameraId() {
        return dock.id;
    }
}
