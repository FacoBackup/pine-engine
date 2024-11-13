package com.pine.panels.viewport;

import com.pine.Engine;
import com.pine.core.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.EditorRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.service.camera.Camera;
import com.pine.service.camera.CameraFirstPersonService;
import com.pine.service.resource.fbo.FrameBufferObject;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;

import static com.pine.core.AbstractWindow.FLAGS;
import static com.pine.core.AbstractWindow.OPEN;
import static com.pine.panels.viewport.ViewportPanel.INV_X;
import static com.pine.panels.viewport.ViewportPanel.INV_Y;

public class FullScreenViewportPanel extends AbstractView {
    private static final ImVec2 PADDING_NONE = new ImVec2(0,0);
    @PInject
    public Engine engine;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public RuntimeRepository repo;

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public CameraFirstPersonService cameraFirstPersonService;

    private FrameBufferObject fbo;
    private final ImVec2 sizeVec = new ImVec2();
    private boolean isFirstMovement;
    private ImGuiIO io;

    @Override
    public void onInitialize() {
        this.fbo = new FrameBufferObject(engine.runtimeRepository.getDisplayW(), engine.runtimeRepository.getDisplayH()).addSampler();
        io = ImGui.getIO();
    }

    @Override
    public void render() {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(ImGui.getMainViewport().getSize());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, PADDING_NONE);
        ImGui.begin("FullScreenViewportPanel", OPEN, FLAGS);
        ImGui.popStyleVar();
        updateCamera();
        tick();
        renderFrame();
        ImGui.end();

        if(ImGui.begin("fps")){
            ImGui.text(io.getFramerate() + "fps");
        }
        ImGui.end();
    }

    private void renderFrame() {
        engine.setTargetFBO(fbo);
        engine.render();
        ImGui.image(fbo.getMainSampler(), sizeVec, INV_Y, INV_X);
    }

    private void updateCamera() {
        var camera = editorRepository.viewportCamera.get(imguiId);
        if (camera == null) {
            editorRepository.viewportCamera.put(imguiId, camera = new Camera());
            camera.pitch = (float) -(Math.PI / 4);
            camera.yaw = (float) (Math.PI / 4);
            camera.orbitalMode = false;
        }
        cameraRepository.setCurrentCamera(camera);

        boolean focused = ImGui.isWindowHovered() && !ImGuizmo.isUsing();
        if (focused && ((ImGui.isMouseDown(ImGuiMouseButton.Right) && !camera.orbitalMode) || (ImGui.isMouseDown(ImGuiMouseButton.Middle) && camera.orbitalMode))) {
            cameraFirstPersonService.handleInput(camera, isFirstMovement);
            isFirstMovement = false;
        } else {
            isFirstMovement = true;
        }
    }

    private void tick() {

        sizeVec.x = ImGui.getWindowSizeX();
        sizeVec.y = ImGui.getWindowSizeY();
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
        repo.viewportH = sizeVec.y;
        repo.viewportW = sizeVec.x;
        repo.viewportX = 0;
        repo.viewportY = 0;
    }


    @Override
    public void onRemove() {
        editorRepository.viewportCamera.remove(imguiId);
        fbo.dispose();
    }
}
