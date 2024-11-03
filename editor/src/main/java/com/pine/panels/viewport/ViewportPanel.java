package com.pine.panels.viewport;

import com.pine.Engine;
import com.pine.injection.PInject;
import com.pine.panels.AbstractEntityViewPanel;
import com.pine.repository.*;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.ViewportPickingService;
import com.pine.service.camera.AbstractCameraService;
import com.pine.service.camera.Camera;
import com.pine.service.camera.CameraFirstPersonService;
import com.pine.service.camera.CameraThirdPersonService;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.ref.TextureResourceRef;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector3f;

import static com.pine.core.dock.DockPanel.OPEN;
import static com.pine.core.dock.DockSpacePanel.FRAME_SIZE;

public class ViewportPanel extends AbstractEntityViewPanel {
    private static final int CAMERA_FLAGS = ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse;
    private static final ImVec4 RED = new ImVec4(1, 0, 0, 1);
    private static final ImVec4 GREEN = new ImVec4(0, 1, 0, 1);
    private static final ImVec4 BLUE = new ImVec4(0, .5f, 1, 1);
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

    @PInject
    public StreamingService streamingService;

    @PInject
    public TerrainRepository terrainRepository;

    private FrameBufferObject fbo;
    private final ImVec2 sizeVec = new ImVec2();
    private GizmoPanel gizmo;
    private ImGuiIO io;
    private boolean isFirstMovement;

    @Override
    public void onInitialize() {
        this.fbo = new FrameBufferObject(engine.runtimeRepository.getDisplayW(), engine.runtimeRepository.getDisplayH()).addSampler();
        appendChild(gizmo = new GizmoPanel(position, sizeVec));
        io = ImGui.getIO();
    }

    @Override
    public void render() {
        var targetTexture = (TextureResourceRef) streamingService.stream(terrainRepository.instanceMaskMap, StreamableResourceType.TEXTURE);
        if(targetTexture != null){
            ImGui.setNextWindowSize(150, 150);
            if(ImGui.begin("image##v")){
                ImGui.image(targetTexture.texture, ImGui.getWindowSize(), INV_Y, INV_X);
            }
            ImGui.end();
        }

        updateCamera();
        hotKeys();
        tick();

        renderFrame();

        gizmo.render();
        renderCameraPosition();
    }

    private void renderFrame() {
        engine.render();
        ImGui.image(engine.getTargetFBO().getMainSampler(), sizeVec, INV_Y, INV_X);
    }

    private void renderCameraPosition() {
        ImGui.setNextWindowPos(position.x + 8, position.y + size.y - 25);
        ImGui.setNextWindowSize(size.x - 16, 16);
        if (ImGui.begin(imguiId + "cameraPos", OPEN, CAMERA_FLAGS)) {
            Vector3f positionCamera = cameraRepository.currentCamera.position;
            ImGui.textColored(RED, "X: " + positionCamera.x);
            ImGui.sameLine();
            ImGui.textColored(GREEN, "Y: " + positionCamera.y);
            ImGui.sameLine();
            ImGui.textColored(BLUE, "Z: " + positionCamera.z);
            ImGui.end();
        }
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

        boolean focused = ImGui.isWindowFocused() && !ImGuizmo.isUsing();
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
        repo.fasterPressed = ImGui.isKeyDown(ImGuiKey.LeftShift);
        repo.forwardPressed = ImGui.isKeyDown(ImGuiKey.W);
        repo.backwardPressed = ImGui.isKeyDown(ImGuiKey.S);
        repo.leftPressed = ImGui.isKeyDown(ImGuiKey.A);
        repo.rightPressed = ImGui.isKeyDown(ImGuiKey.D);
        repo.upPressed = ImGui.isKeyDown(ImGuiKey.Space);
        repo.downPressed = ImGui.isKeyDown(ImGuiKey.LeftCtrl);
        repo.mousePressed = ImGui.isMouseDown(ImGuiMouseButton.Left);
        repo.mouseX = ImGui.getMousePosX();
        repo.mouseY = ImGui.getMousePosY();
        repo.viewportH = sizeVec.y;
        repo.viewportW = sizeVec.x;
        repo.viewportX = position.x;
        repo.viewportY = position.y + FRAME_SIZE;
    }

    @Override
    protected void hotKeysInternal() {
        if (ImGui.isKeyPressed(ImGuiKey.T))
            editorRepository.gizmoType = GizmoType.TRANSLATE;
        if (ImGui.isKeyPressed(ImGuiKey.R))
            editorRepository.gizmoType = GizmoType.ROTATE;
        if (ImGui.isKeyPressed(ImGuiKey.Y))
            editorRepository.gizmoType = GizmoType.SCALE;

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
