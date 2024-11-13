package com.pine.panels.viewport;

import com.pine.component.TransformationComponent;
import com.pine.core.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.EditorRepository;
import com.pine.service.SelectionService;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Operation;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GizmoPanel extends AbstractView {
    @PInject
    public EditorRepository stateRepository;

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public SelectionService selectionService;

    private final Matrix4f auxMat4 = new Matrix4f();
    private final Vector3f auxTranslation = new Vector3f();
    private final Vector3f auxScale = new Vector3f();
    private final Quaternionf auxRot = new Quaternionf();

    private final float[] cacheMatrix = new float[16];
    private final float[] viewMatrixCache = new float[16];
    private final float[] projectionMatrixCache = new float[16];
    private final ImVec2 size;
    private final ImVec2 position;
    private TransformationComponent localSelected;
    private int localChangeId;

    public GizmoPanel(ImVec2 position, ImVec2 size) {
        this.size = size;
        this.position = position;
    }

    @Override
    public void render() {
        if (stateRepository.primitiveSelected == null) {
            localSelected = null;
            localChangeId = 0;
            if(stateRepository.mainSelection != null){
                selectionService.updatePrimitiveSelected();
            }
            return;
        }

        if (stateRepository.primitiveSelected != localSelected || localSelected.getChangeId() != localChangeId) {
            stateRepository.primitiveSelected.modelMatrix.get(cacheMatrix);
            localSelected = stateRepository.primitiveSelected;
            getLogger().warn("Updating gizmo {} {}", stateRepository.primitiveSelected != localSelected, localSelected.getChangeId() != localChangeId);
            localChangeId = localSelected.getChangeId();
        }

        recomposeMatrix();
        float[] snap = getSnapValues();
        ImGuizmo.setOrthographic(cameraRepository.currentCamera.isOrthographic);
        ImGuizmo.setDrawList();
        ImGuizmo.setRect(position.x, position.y, size.x, size.y);
        ImGuizmo.manipulate(
                viewMatrixCache,
                projectionMatrixCache,
                stateRepository.gizmoType,
                stateRepository.gizmoMode,
                cacheMatrix,
                null,
                snap);
        if (ImGuizmo.isUsing()) {
            decomposeMatrix();
        }
    }

    private float @Nullable [] getSnapValues() {
        return switch (stateRepository.gizmoType) {
            case Operation.TRANSLATE -> {
                if (stateRepository.gizmoUseSnapTranslate) {
                    yield stateRepository.gizmoSnapTranslate;
                }
                yield null;
            }
            case Operation.ROTATE -> {
                if (stateRepository.gizmoUseSnapRotate) {
                    yield stateRepository.gizmoSnapRotate.getData();
                }
                yield null;
            }
            case Operation.SCALE -> {
                if (stateRepository.gizmoUseSnapScale) {
                    yield stateRepository.gizmoSnapScale.getData();
                }
                yield null;
            }
            default -> null;
        };
    }

    private void decomposeMatrix() {
        auxMat4.set(cacheMatrix);
        auxMat4.getTranslation(auxTranslation);
        auxMat4.getUnnormalizedRotation(auxRot);
        auxMat4.getScale(auxScale);

        auxTranslation.sub(localSelected.translation);
        auxScale.sub(localSelected.scale);
        auxRot.sub(localSelected.rotation);

        localSelected.translation.add(auxTranslation);
        localSelected.scale.add(auxScale);
        localSelected.rotation.add(auxRot);

        localSelected.registerChange();
        localChangeId =  localSelected.getChangeId();
    }

    private void recomposeMatrix() {
        cameraRepository.viewMatrix.get(viewMatrixCache);
        cameraRepository.projectionMatrix.get(projectionMatrixCache);
    }
}
