package com.pine.panels.viewport;

import com.pine.component.Transformation;
import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.EditorRepository;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Operation;
import org.jetbrains.annotations.Nullable;

public class GizmoPanel extends AbstractView {
    @PInject
    public EditorRepository stateRepository;

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
    public void render() {
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
        Transformation p = stateRepository.primitiveSelected;
        p.localMatrix.set(cacheMatrix);

        p.localMatrix.getTranslation(p.translation);
        p.localMatrix.getUnnormalizedRotation(p.rotation);
        p.localMatrix.getScale(p.scale);

        p.registerChange();
    }

    private void recomposeMatrix() {
        stateRepository.primitiveSelected.localMatrix.get(cacheMatrix);
        cameraRepository.viewMatrix.get(viewMatrixCache);
        cameraRepository.projectionMatrix.get(projectionMatrixCache);
    }
}
