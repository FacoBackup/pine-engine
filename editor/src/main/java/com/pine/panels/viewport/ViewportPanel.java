package com.pine.panels.viewport;

import com.pine.Engine;
import com.pine.PInject;
import com.pine.dock.AbstractDockPanel;
import com.pine.repository.EditorStateRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.service.SelectionService;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.fbo.FBOCreationData;
import com.pine.service.resource.fbo.FrameBufferObject;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiKey;

import static com.pine.dock.DockWrapperPanel.FRAME_SIZE;

public class ViewportPanel extends AbstractDockPanel {
    @PInject
    public Engine engine;

    @PInject
    public RuntimeRepository repo;

    @PInject
    public ResourceService resourceService;

    @PInject
    public SelectionService selectionService;


    @PInject
    public EditorStateRepository stateRepository;

    private FrameBufferObject fbo;
    private final ImVec2 sizeVec = new ImVec2();
    private final ImVec2 INV_X = new ImVec2(1, 0);
    private final ImVec2 INV_Y = new ImVec2(0, 1);
    private GizmoPanel gizmo;
    private GizmoConfigPanel gizmoPanel;

    @Override
    public void onInitialize() {
        super.onInitialize();
        this.fbo = (FrameBufferObject) resourceService.addResource(new FBOCreationData(false, false).addSampler());
        appendChild(gizmoPanel = new GizmoConfigPanel(position, sizeVec));
        appendChild(gizmo = new GizmoPanel(position, sizeVec));
    }

    @Override
    public void tick() {
        engine.setTargetFBO(fbo);
        engine.render();
    }

    @Override
    public void renderInternal() {
        afterWindow();

        sizeVec.x = size.x;
        sizeVec.y = size.y - FRAME_SIZE;

        gizmoPanel.renderInternal();
        ImGui.image(engine.getTargetFBO().getMainSampler(), sizeVec, INV_Y, INV_X);

        gizmo.renderInternal();
    }

    private void afterWindow() {
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
