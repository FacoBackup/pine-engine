package com.pine.panels.viewport;

import com.pine.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.EditorStateRepository;
import com.pine.view.AbstractView;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Operation;
import org.jetbrains.annotations.Nullable;

public class GizmoPanel extends AbstractView {
    @PInject
    public EditorStateRepository stateRepository;

    @PInject
    public CameraRepository cameraRepository;

    private final float[] cacheMatrix = new float[16];
    private final float[] viewMatrixCache = new float[16];
    private final float[] projectionMatrixCache = new float[16];
    private final float[] translationCache = new float[3];
    private final float[] rotationCache = new float[3];
    private final float[] scaleCache = new float[3];
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
        decomposeMatrix();
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
        ImGuizmo.decomposeMatrixToComponents(cacheMatrix, translationCache, rotationCache, scaleCache);
       stateRepository.primitiveSelected.translation.x = translationCache[0];
       stateRepository.primitiveSelected.translation.y = translationCache[1];
       stateRepository.primitiveSelected.translation.z = translationCache[2];

       stateRepository.primitiveSelected.rotation.x = rotationCache[0];
       stateRepository.primitiveSelected.rotation.y = rotationCache[1];
       stateRepository.primitiveSelected.rotation.z = rotationCache[2];

       stateRepository.primitiveSelected.scale.x = scaleCache[0];
       stateRepository.primitiveSelected.scale.y = scaleCache[1];
       stateRepository.primitiveSelected.scale.z = scaleCache[2];
    }

    private void recomposeMatrix() {
        translationCache[0] = stateRepository.primitiveSelected.translation.x;
        translationCache[1] = stateRepository.primitiveSelected.translation.y;
        translationCache[2] = stateRepository.primitiveSelected.translation.z;

        rotationCache[0] = stateRepository.primitiveSelected.rotation.x;
        rotationCache[1] = stateRepository.primitiveSelected.rotation.y;
        rotationCache[2] = stateRepository.primitiveSelected.rotation.z;

        scaleCache[0] = stateRepository.primitiveSelected.scale.x;
        scaleCache[1] = stateRepository.primitiveSelected.scale.y;
        scaleCache[2] = stateRepository.primitiveSelected.scale.z;

        cameraRepository.currentCamera.viewMatrix.get(viewMatrixCache);
        cameraRepository.currentCamera.projectionMatrix.get(projectionMatrixCache);

        ImGuizmo.recomposeMatrixFromComponents(translationCache, rotationCache, scaleCache, cacheMatrix);
    }
}
