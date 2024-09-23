package com.pine.panels.viewport;

import com.pine.Engine;
import com.pine.PInject;
import com.pine.component.rendering.SimpleTransformation;
import com.pine.repository.EntitySelectionRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.fbo.FBOCreationData;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.world.WorldService;
import com.pine.ui.panel.AbstractWindowPanel;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiKey;

public class ViewportPanel extends AbstractWindowPanel {
    @PInject
    public Engine engine;

    @PInject
    public RuntimeRepository repo;

    @PInject
    public ResourceService resourceService;

    @PInject
    public EntitySelectionRepository entitySelectionRepository;

    private FrameBufferObject fbo;
    private final ImVec2 sizeVec = new ImVec2();
    private final ImVec2 INV_X = new ImVec2(1, 0);
    private final ImVec2 INV_Y = new ImVec2(0, 1);
    private SimpleTransformation selected;
    private GizmoPanel gizmo;
    private GizmoConfigPanel gizmoPanel;

    @Override
    public void onInitialize() {
        super.onInitialize();
        padding.x = 0;
        padding.y = 0;
        this.fbo = (FrameBufferObject) resourceService.addResource(new FBOCreationData(false, false).addSampler());
        appendChild(gizmoPanel = new GizmoConfigPanel(position, sizeVec));
        appendChild(gizmo = new GizmoPanel(position, sizeVec));
    }

    @Override
    protected String getTitle() {
        return "Viewport";
    }

    @Override
    public void tick() {
        engine.setTargetFBO(fbo);
        engine.render();
    }

    @Override
    public void renderInternal() {
        sizeVec.x = size.x;
        sizeVec.y = size.y - FRAME_SIZE;
        ImGui.image(engine.getTargetFBO().getMainSampler(), sizeVec, INV_Y, INV_X);
        if (selected != entitySelectionRepository.getPrimitiveSelected()) {
            selected = entitySelectionRepository.getPrimitiveSelected();
        }

        gizmoPanel.renderInternal();
        if (selected == null) {
            return;
        }
        gizmo.setSelected(selected);
        gizmo.renderInternal();
    }

    @Override
    protected void afterWindow() {
        repo.inputFocused = ImGui.isWindowFocused() && (ImGui.isMouseDown(2) || ImGui.isMouseDown(1));
        repo.fasterPressed = ImGui.isKeyPressed(ImGuiKey.LeftShift);
        repo.forwardPressed = ImGui.isKeyPressed(ImGuiKey.W);
        repo.backwardPressed = ImGui.isKeyPressed(ImGuiKey.S);
        repo.leftPressed = ImGui.isKeyPressed(ImGuiKey.A);
        repo.rightPressed = ImGui.isKeyPressed(ImGuiKey.D);
        repo.upPressed = ImGui.isKeyPressed(ImGuiKey.Space);
        repo.downPressed = ImGui.isKeyPressed(ImGuiKey.LeftCtrl);
        repo.mouseX = ImGui.getMousePosX();
        repo.mouseY = ImGui.getMousePosY();
        repo.viewportH = size.y;
        repo.viewportW = size.x;
    }
}
