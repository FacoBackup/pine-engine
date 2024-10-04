package com.pine.panels.viewport;

import com.pine.Engine;
import com.pine.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.service.camera.AbstractCameraService;
import com.pine.service.camera.CameraFirstPersonService;
import com.pine.service.camera.CameraThirdPersonService;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.fbo.FBOCreationData;
import com.pine.service.resource.fbo.FrameBufferObject;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseButton;

import static com.pine.dock.DockWrapperPanel.FRAME_SIZE;

public class ViewportPanel extends AbstractDockPanel {
    @PInject
    public Engine engine;

    @PInject
    public RuntimeRepository repo;

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public ResourceService resourceService;

    @PInject
    public CameraThirdPersonService cameraThirdPersonService;

    @PInject
    public CameraFirstPersonService cameraFirstPersonService;

    private AbstractCameraService cameraService;

    private FrameBufferObject fbo;
    private final ImVec2 sizeVec = new ImVec2();
    private final ImVec2 INV_X = new ImVec2(1, 0);
    private final ImVec2 INV_Y = new ImVec2(0, 1);
    private GizmoPanel gizmo;
    private ViewportHeaderPanel gizmoPanel;
    private ViewportContext context;
    private ImGuiIO io;
    private boolean isFirstMovement;

    @Override
    public void onInitialize() {

        this.fbo = (FrameBufferObject) resourceService.addResource(new FBOCreationData(false, false).addSampler());
        appendChild(gizmoPanel = new ViewportHeaderPanel(sizeVec));
        appendChild(gizmo = new GizmoPanel(position, sizeVec));
        context = (ViewportContext) getContext();
        io = ImGui.getIO();
    }

    @Override
    public void tick() {
        cameraRepository.setCurrentCamera(context.camera);
        updateCamera();
        engine.setTargetFBO(fbo);
        engine.render();
    }

    @Override
    public void renderInternal() {
        sizeVec.x = size.x;
        sizeVec.y = size.y - FRAME_SIZE - ViewportHeaderPanel.SIZE;

        gizmoPanel.renderInternal();
        ImGui.image(engine.getTargetFBO().getMainSampler(), sizeVec, INV_Y, INV_X);

        gizmo.renderInternal();
    }

    private void updateCamera() {
        if (ImGui.isWindowFocused() && (ImGui.isMouseDown(ImGuiMouseButton.Left) || ImGui.isMouseDown(ImGuiMouseButton.Right) || (ImGui.isMouseDown(ImGuiMouseButton.Middle) && context.camera.orbitalMode))) {
            cameraService.handleInput(context.camera, isFirstMovement);
            isFirstMovement = false;
        } else {
            isFirstMovement = true;
        }
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

        if (context.camera.orbitalMode) {
            cameraService = cameraThirdPersonService;
            if (ImGui.isWindowFocused()) {
                if (io.getMouseWheel() != 0) {
                    cameraThirdPersonService.zoom(context.camera, io.getMouseWheel());
                }
                if (io.getMouseDown(ImGuiMouseButton.Left) && io.getMouseDown(ImGuiMouseButton.Right)) {
                    cameraThirdPersonService.isChangingCenter = true;
                    cameraThirdPersonService.changeCenter(context.camera, isFirstMovement);
                } else {
                    cameraThirdPersonService.isChangingCenter = false;
                }
            }
        } else {
            cameraService = cameraFirstPersonService;
        }
    }
}
