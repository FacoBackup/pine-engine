package com.pine.panels.viewport;

import com.pine.Engine;
import com.pine.core.AbstractView;
import com.pine.injection.PInject;
import com.pine.panels.AbstractEntityViewPanel;
import com.pine.repository.CameraRepository;
import com.pine.repository.EditorMode;
import com.pine.repository.EditorRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.service.ViewportPickingService;
import com.pine.service.camera.AbstractCameraService;
import com.pine.service.camera.Camera;
import com.pine.service.camera.CameraFirstPersonService;
import com.pine.service.camera.CameraThirdPersonService;
import com.pine.service.resource.fbo.FrameBufferObject;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseButton;

import static com.pine.core.dock.DockSpacePanel.FRAME_SIZE;

public class ViewportPanel extends AbstractEntityViewPanel {

    public static final ImVec2 INV_X = new ImVec2(1, 0);
    public static final ImVec2 INV_Y = new ImVec2(0, 1);

    @PInject
    public Engine engine;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public RuntimeRepository repo;

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public CameraThirdPersonService cameraThirdPersonService;

    @PInject
    public CameraFirstPersonService cameraFirstPersonService;

    @PInject
    public ViewportPickingService viewportPickingService;

    private FrameBufferObject fbo;
    private final ImVec2 sizeVec = new ImVec2();
    private ImGuiIO io;
    private boolean isFirstMovement;
    private AbstractView headerPanel;
    private AbstractView cameraPanel;
    private AbstractView gizmoPanel;

    @Override
    public void onInitialize() {
        this.fbo = new FrameBufferObject(engine.runtimeRepository.getDisplayW(), engine.runtimeRepository.getDisplayH()).addSampler();
        io = ImGui.getIO();
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

        if(editorRepository.editorMode == EditorMode.TRANSFORM){
            gizmoPanel.render();
        }
        headerPanel.render();
        cameraPanel.render();
    }

    private void renderFrame() {
        engine.render();
        ImGui.image(engine.getTargetFBO().getMainSampler(), sizeVec, INV_Y, INV_X);
    }

    private void updateCamera() {
        var camera = editorRepository.viewportCamera.get(this.dock.id);
        if (camera == null) {
            editorRepository.viewportCamera.put(this.dock.id, camera = new Camera());
            camera.pitch = (float) -(Math.PI / 4);
            camera.yaw = (float) (Math.PI / 4);
            camera.orbitalMode = true;
        }
        cameraRepository.setCurrentCamera(camera);

        boolean focused = ImGui.isWindowHovered() && !ImGuizmo.isUsing();
        AbstractCameraService cameraService;
        if (camera.orbitalMode) {
            cameraService = cameraThirdPersonService;
            if (focused) {
                if (io.getMouseWheel() != 0 && ImGui.isWindowHovered()) {
                    cameraThirdPersonService.zoom(camera, io.getMouseWheel());
                }
                if (io.getMouseDown(ImGuiMouseButton.Right)) {
                    cameraThirdPersonService.isChangingCenter = true;
                    cameraThirdPersonService.changeCenter(camera, isFirstMovement);
                } else {
                    cameraThirdPersonService.isChangingCenter = false;
                }
            }
        } else {
            cameraService = cameraFirstPersonService;
        }
        if (focused && ((ImGui.isMouseDown(ImGuiMouseButton.Right) && !camera.orbitalMode) || (ImGui.isMouseDown(ImGuiMouseButton.Middle) && camera.orbitalMode))) {
            cameraService.handleInput(camera, isFirstMovement);
            isFirstMovement = false;
        } else {
            isFirstMovement = true;
        }
    }

    private void tick() {
        engine.setTargetFBO(fbo);

        sizeVec.x = size.x;
        sizeVec.y = size.y - FRAME_SIZE;

        repo.viewportH = sizeVec.y;
        repo.viewportW = sizeVec.x;
        repo.viewportX = position.x;
        repo.viewportY = position.y + FRAME_SIZE;

        repo.isFocused = ImGui.isWindowHovered();
        repo.fasterPressed = ImGui.isKeyDown(ImGuiKey.LeftShift);
        repo.forwardPressed = ImGui.isKeyDown(ImGuiKey.W);
        repo.backwardPressed = ImGui.isKeyDown(ImGuiKey.S);
        repo.leftPressed = ImGui.isKeyDown(ImGuiKey.A);
        repo.rightPressed = ImGui.isKeyDown(ImGuiKey.D);
        repo.upPressed = ImGui.isKeyDown(ImGuiKey.Space);
        repo.downPressed = ImGui.isKeyDown(ImGuiKey.LeftCtrl);
        repo.mousePressed = ImGui.isWindowFocused() && ImGui.isWindowHovered() && ImGui.isMouseDown(ImGuiMouseButton.Left);
        repo.mouseX = ImGui.getMousePosX();
        repo.mouseY = ImGui.getMousePosY();
    }

    @Override
    protected void hotKeysInternal() {
        if (ImGui.isKeyPressed(ImGuiKey.T))
            editorRepository.gizmoType = Operation.TRANSLATE;
        if (ImGui.isKeyPressed(ImGuiKey.R))
            editorRepository.gizmoType = Operation.ROTATE;
        if (ImGui.isKeyPressed(ImGuiKey.Y))
            editorRepository.gizmoType = Operation.SCALE;

        if (ImGui.isWindowHovered() && !ImGuizmo.isOver() && ImGui.isMouseClicked(ImGuiMouseButton.Left)) {
            viewportPickingService.pick();
        }
    }

    @Override
    public void onRemove() {
        editorRepository.viewportCamera.remove(dock.id);
        fbo.dispose();
    }
}
