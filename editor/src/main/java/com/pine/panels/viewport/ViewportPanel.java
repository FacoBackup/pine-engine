package com.pine.panels.viewport;

import com.pine.Engine;
import com.pine.PInject;
import com.pine.component.TransformationComponent;
import com.pine.repository.*;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.fbo.FBOCreationData;
import com.pine.service.resource.fbo.FrameBufferObject;
import com.pine.service.world.WorldService;
import com.pine.ui.panel.AbstractWindowPanel;
import imgui.*;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiKey;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Matrix4f;

import java.util.Objects;

import static java.lang.foreign.MemorySegment.NULL;

public class ViewportPanel extends AbstractWindowPanel {
    @PInject
    public Engine engine;

    @PInject
    public RuntimeRepository repo;

    @PInject
    public ResourceService resourceService;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public EntitySelectionRepository entitySelectionRepository;

    @PInject
    public WorldService world;

    private FrameBufferObject fbo;
    private final ImVec2 sizeVec = new ImVec2();
    private final ImVec2 INV_X = new ImVec2(1, 0);
    private final ImVec2 INV_Y = new ImVec2(0, 1);
    private final float[] cacheMatrix = new float[16];
    private final float[] viewMatrixCache = new float[16];
    private final float[] projectionMatrixCache = new float[16];
    private final float[] translationCache = new float[3];
    private final float[] rotationCache = new float[3];
    private final float[] scaleCache = new float[3];
    private TransformationComponent selected;
    private GizmoPanel gizmo;

    @Override
    public void onInitialize() {
        super.onInitialize();
        padding.x = 0;
        padding.y = 0;
        this.fbo = (FrameBufferObject) resourceService.addResource(new FBOCreationData(false, false).addSampler());
        appendChild(new GizmoConfigPanel(position, sizeVec));
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
        Integer mainSelection = entitySelectionRepository.getMainSelection();
        if (mainSelection != null && (selected == null || !Objects.equals(mainSelection, selected.getEntityId()))) {
            selected = world.getTransformationComponentUnchecked(mainSelection);
        } else if (mainSelection == null) {
            selected = null;
        }

        if (selected == null) {
            return;
        }

        gizmo.setSelected(selected);
        super.renderInternal();
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
