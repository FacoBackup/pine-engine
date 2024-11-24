package com.pine.panels.viewport;

import com.pine.Engine;
import com.pine.injection.PInject;
import com.pine.panels.AbstractEntityViewPanel;
import com.pine.repository.CameraRepository;
import com.pine.repository.EditorRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.service.camera.Camera;
import com.pine.service.camera.CameraMovementService;
import com.pine.service.resource.fbo.FBO;
import com.pine.service.resource.fbo.FBOCreationData;
import com.pine.service.resource.fbo.FBOService;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseButton;

import static com.pine.core.dock.DockSpacePanel.FRAME_SIZE;
import static com.pine.panels.viewport.ViewportPanel.INV_X;
import static com.pine.panels.viewport.ViewportPanel.INV_Y;

public abstract class AbstractViewportPanel extends AbstractEntityViewPanel {
    @PInject
    public Engine engine;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public RuntimeRepository repo;

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public CameraMovementService cameraMovementService;

    @PInject
    public FBOService fboService;

    private FBO fbo;
    protected final ImVec2 sizeVec = new ImVec2();
    private boolean isFirstMovement;
    protected ImGuiIO io;

    @Override
    public void onInitialize() {
        this.fbo = fboService.create(new FBOCreationData(engine.runtimeRepository.getDisplayW(), engine.runtimeRepository.getDisplayH(), false, false).addSampler());
        io = ImGui.getIO();
    }

    final protected void renderFrame() {
        engine.render();
        ImGui.image(engine.getTargetFBO().getMainSampler(), sizeVec, INV_Y, INV_X);
    }

    final protected void updateCamera() {
        var camera = editorRepository.viewportCamera.get(getCameraId());
        if (camera == null) {
            editorRepository.viewportCamera.put(getCameraId(), camera = new Camera());
            camera.pitch = (float) -(Math.PI / 4);
            camera.yaw = (float) (Math.PI / 4);
            camera.position.set(10, 10, 10);
        }
        cameraRepository.setCurrentCamera(camera);

        if (ImGui.isWindowHovered() && !ImGuizmo.isUsing() && ImGui.isMouseDown(ImGuiMouseButton.Right)) {
            cameraMovementService.handleInput(camera, isFirstMovement);
            if (io.getMouseWheel() != 0) {
                cameraRepository.movementSensitivity += io.getMouseWheel() * 100 * cameraMovementService.clockRepository.deltaTime;
                cameraRepository.movementSensitivity = Math.max(.1f, cameraRepository.movementSensitivity);
            }
            isFirstMovement = false;
        } else {
            isFirstMovement = true;
        }
    }

    final protected void tick() {
        engine.setTargetFBO(fbo);

        sizeVec.x = ImGui.getWindowSizeX();
        sizeVec.y = ImGui.getWindowSizeY() - FRAME_SIZE;

        repo.viewportH = sizeVec.y;
        repo.viewportW = sizeVec.x;
        repo.viewportX = ImGui.getWindowPosX();
        repo.viewportY = ImGui.getWindowPosY() + FRAME_SIZE;

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

        repo.normalizedMouseX = (repo.mouseX + repo.viewportX) / repo.viewportW;
        repo.normalizedMouseY = (repo.viewportH - repo.mouseY + repo.viewportY) / repo.viewportH;
    }

    @Override
    final public void onRemove() {
        editorRepository.viewportCamera.remove(getCameraId());
        fbo.dispose();
    }

    protected abstract String getCameraId();
}
