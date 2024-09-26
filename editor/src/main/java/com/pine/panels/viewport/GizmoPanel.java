package com.pine.panels.viewport;

import com.pine.PInject;
import com.pine.component.rendering.SimpleTransformation;
import com.pine.repository.CameraRepository;
import com.pine.repository.EditorSettingsRepository;
import com.pine.view.AbstractView;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Operation;

public class GizmoPanel extends AbstractView {
    @PInject
    public EditorSettingsRepository editorSettingsRepository;

    @PInject
    public CameraRepository cameraRepository;

    private final float[] cacheMatrix = new float[16];
    private final float[] viewMatrixCache = new float[16];
    private final float[] projectionMatrixCache = new float[16];
    private final float[] translationCache = new float[3];
    private final float[] rotationCache = new float[3];
    private final float[] scaleCache = new float[3];
    private SimpleTransformation selected;
    private final ImVec2 size;
    private final ImVec2 position;

    public GizmoPanel(ImVec2 position, ImVec2 size) {
        this.size = size;
        this.position = position;
    }

    @Override
    public void renderInternal() {
        recomposeMatrix();
        float[] snap = switch (editorSettingsRepository.gizmoOperation) {
            case Operation.TRANSLATE -> editorSettingsRepository.gizmoSnapTranslate;
            case Operation.ROTATE -> editorSettingsRepository.gizmoSnapRotate.getData();
            case Operation.SCALE -> editorSettingsRepository.gizmoSnapScale.getData();
            default -> null;
        };
        ImGuizmo.setOrthographic(cameraRepository.currentCamera.isOrthographic);
        ImGuizmo.setDrawList();
        ImGuizmo.setRect(position.x, position.y, size.x, size.y);
        ImGuizmo.manipulate(
                viewMatrixCache,
                projectionMatrixCache,
                editorSettingsRepository.gizmoOperation,
                editorSettingsRepository.gizmoMode,
                cacheMatrix,
                null,
                editorSettingsRepository.gizmoUseSnap || snap == null ? snap : null);
        decomposeMatrix();
    }

    private void decomposeMatrix() {
        ImGuizmo.decomposeMatrixToComponents(cacheMatrix, translationCache, rotationCache, scaleCache);
        selected.translation.x = translationCache[0];
        selected.translation.y = translationCache[1];
        selected.translation.z = translationCache[2];

        selected.rotation.x = rotationCache[0];
        selected.rotation.y = rotationCache[1];
        selected.rotation.z = rotationCache[2];

        selected.scale.x = scaleCache[0];
        selected.scale.y = scaleCache[1];
        selected.scale.z = scaleCache[2];
    }

    private void recomposeMatrix() {
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
    }

    public void setSelected(SimpleTransformation selected) {
        this.selected = selected;
    }
}
