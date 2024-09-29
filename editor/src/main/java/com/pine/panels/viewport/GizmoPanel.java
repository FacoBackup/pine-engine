package com.pine.panels.viewport;

import com.pine.PInject;
import com.pine.component.TransformationComponent;
import com.pine.repository.CameraRepository;
import com.pine.repository.EditorStateRepository;
import com.pine.view.AbstractView;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Operation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GizmoPanel extends AbstractView {
    @PInject
    public EditorStateRepository stateRepository;

    @PInject
    public CameraRepository cameraRepository;

    private final float[] cacheMatrix = new float[16];
    private final float[] viewMatrixCache = new float[16];
    private final float[] projectionMatrixCache = new float[16];
    private final ImVec2 size;
    private final ImVec2 position;

    public GizmoPanel(ImVec2 position, ImVec2 size) {
        this.size = size;
        this.position = position;
    }

    @Override
    public void renderInternal() {
        if (stateRepository.primitiveSelected == null) {
            return;
        }
        recomposeMatrix();
        float[] snap = getSnapValues();
        ImGuizmo.setOrthographic(cameraRepository.currentCamera.isOrthographic);
        ImGuizmo.setDrawList();
        ImGuizmo.setRect(position.x, position.y, size.x, size.y);
        ImGuizmo.manipulate(
                viewMatrixCache,
                projectionMatrixCache,
                stateRepository.gizmoOperation,
                stateRepository.gizmoMode,
                cacheMatrix,
                null,
                snap);
        if (ImGuizmo.isUsing()) {
            decomposeMatrix();
        }
    }

    private float @Nullable [] getSnapValues() {
        return switch (stateRepository.gizmoOperation) {
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
        TransformationComponent p = stateRepository.primitiveSelected;
        p.matrix.set(cacheMatrix);

        p.matrix.getTranslation(p.translation);
        p.matrix.getUnnormalizedRotation(p.rotation);
        p.matrix.getScale(p.scale);

        p.registerChange();
    }

    private void recomposeMatrix() {
        stateRepository.primitiveSelected.matrix.get(cacheMatrix);
        cameraRepository.currentCamera.viewMatrix.get(viewMatrixCache);
        cameraRepository.currentCamera.projectionMatrix.get(projectionMatrixCache);
    }
}
