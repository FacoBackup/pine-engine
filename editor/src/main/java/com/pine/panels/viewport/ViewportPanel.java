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

    @Override
    public void onInitialize() {
        super.onInitialize();
        padding.x = 0;
        padding.y = 0;
        this.fbo = (FrameBufferObject) resourceService.addResource(new FBOCreationData(false, false).addSampler());
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

        ImGui.setNextWindowPos(position);
        ImGui.setNextWindowSize(size.x, size.y * .2f);
        ImGui.begin("##gizmoOptions", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize);
        if (ImGui.isKeyPressed(ImGuiKey.T))
            editorRepository.gizmoOperation = Operation.TRANSLATE;
        if (ImGui.isKeyPressed(ImGuiKey.R))
            editorRepository.gizmoOperation = Operation.ROTATE;
        if (ImGui.isKeyPressed(ImGuiKey.Y))
            editorRepository.gizmoOperation = Operation.SCALE;

        if (ImGui.radioButton("Translate", editorRepository.gizmoOperation == Operation.TRANSLATE))
            editorRepository.gizmoOperation = Operation.TRANSLATE;
        ImGui.sameLine();
        if (ImGui.radioButton("Rotate", editorRepository.gizmoOperation == Operation.ROTATE))
            editorRepository.gizmoOperation = Operation.ROTATE;
        ImGui.sameLine();
        if (ImGui.radioButton("Scale", editorRepository.gizmoOperation == Operation.SCALE))
            editorRepository.gizmoOperation = Operation.SCALE;

        if (editorRepository.gizmoOperation != Operation.SCALE) {
            ImGui.sameLine();
            if (ImGui.radioButton("Local", editorRepository.gizmoMode == Mode.LOCAL))
                editorRepository.gizmoMode = Mode.LOCAL;
            ImGui.sameLine();
            if (ImGui.radioButton("World", editorRepository.gizmoMode == Mode.WORLD))
                editorRepository.gizmoMode = Mode.WORLD;
        }

        if (ImGui.isKeyPressed(ImGuiKey.F))
            editorRepository.gizmoUseSnap = !editorRepository.gizmoUseSnap;

        ImGui.sameLine();
        ImGui.checkbox("Snap" + internalId, editorRepository.gizmoUseSnap);
        ImGui.sameLine();
        float[] snap = switch (editorRepository.gizmoOperation) {
            case Operation.TRANSLATE -> {
                ImGui.inputFloat3("Snap", editorRepository.gizmoSnapTranslate);
                yield editorRepository.gizmoSnapTranslate;
            }
            case Operation.ROTATE -> {
                ImGui.inputFloat("Angle Snap", editorRepository.gizmoSnapRotate);
                yield editorRepository.gizmoSnapRotate.getData();
            }
            case Operation.SCALE -> {
                ImGui.inputFloat("Scale Snap", editorRepository.gizmoSnapScale);
                yield editorRepository.gizmoSnapScale.getData();
            }
            default -> null;
        };

        translationCache[0] = selected.translation.x;
        translationCache[1] = selected.translation.y;
        translationCache[2] = selected.translation.z;

        rotationCache[0] = selected.rotation.x;
        rotationCache[1] = selected.rotation.y;
        rotationCache[2] = selected.rotation.z;

        scaleCache[0] = selected.scale.x;
        scaleCache[1] = selected.scale.y;
        scaleCache[2] = selected.scale.z;

        cameraRepository.currentCamera.viewMatrix.get(viewMatrixCache);
        cameraRepository.currentCamera.projectionMatrix.get(projectionMatrixCache);

        ImGuizmo.recomposeMatrixFromComponents(translationCache, rotationCache, scaleCache, cacheMatrix);
        ImGui.end();

        ImGuizmo.setOrthographic(false);
        ImGuizmo.setDrawList();
        ImGuizmo.setRect(position.x, position.y, sizeVec.x, sizeVec.y);
        ImGuizmo.manipulate(
                viewMatrixCache,
                projectionMatrixCache,
                editorRepository.gizmoOperation,
                editorRepository.gizmoMode,
                cacheMatrix,
                cacheMatrix,
                editorRepository.gizmoUseSnap || snap == null ? snap : null);

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
