package com.pine.panels.header;

import com.pine.core.AbstractView;
import com.pine.core.UIUtil;
import com.pine.injection.PInject;
import com.pine.repository.*;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.type.ImInt;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class GlobalSettingsPanel extends AbstractView {
    private static final String[] SHADING_MODE_OPTIONS = DebugShadingModel.getLabels();

    private final float[] cameraSensitivity = new float[1];
    private final ImInt editorMode = new ImInt(0);

    @PInject
    public EngineRepository engineRepository;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public CameraRepository cameraRepository;

    @Override
    public void render() {
        editorMode.set(editorRepository.editorMode.index);
        ImGui.setNextItemWidth(150);
        if (ImGui.combo(imguiId, editorMode, EditorMode.getOptions())) {
            editorRepository.editorMode = EditorMode.valueOfIndex(editorMode.get());
        }
        cameraMode();
        shadingMode();
    }


    private void cameraMode() {
        UIUtil.dynamicSpacing(575);
        ImGui.text("Camera");
        ImGui.sameLine();
        if (ImGui.button(Icons.center_focus_strong + "##centerCamera", ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
            cameraRepository.currentCamera.position.zero();
            cameraRepository.currentCamera.registerChange();
        }

        ImGui.sameLine();
        cameraSensitivity[0] = cameraRepository.movementSensitivity;
        ImGui.setNextItemWidth(75);
        if (ImGui.dragFloat( "##speedCamera", cameraSensitivity, .1f, .1f)) {
            cameraRepository.movementSensitivity = Math.max(0, cameraSensitivity[0]);
        }
    }

    private void shadingMode() {
        UIUtil.dynamicSpacing(405);

        ImGui.text("Shading");

        ImGui.sameLine();
        if (UIUtil.renderOption(Icons.grid_on + "##grid", editorRepository.gridOverlayObjects, true, editorRepository.accent)) {
            editorRepository.gridOverlayObjects = !editorRepository.gridOverlayObjects;
        }

        ImGui.sameLine();
        if (UIUtil.renderOption(Icons.details + "Wireframe##wireframeShading", engineRepository.debugShadingModel == DebugShadingModel.WIREFRAME, false, editorRepository.accent)) {
            engineRepository.debugShadingModel = DebugShadingModel.WIREFRAME;
            editorRepository.shadingModelOption.set(DebugShadingModel.WIREFRAME.getIndex());
        }

        ImGui.sameLine();
        if (UIUtil.renderOption(Icons.palette + "Random##randomShading", engineRepository.debugShadingModel == DebugShadingModel.RANDOM, false, editorRepository.accent)) {
            engineRepository.debugShadingModel = DebugShadingModel.RANDOM;
            editorRepository.shadingModelOption.set(DebugShadingModel.RANDOM.getIndex());
        }

        ImGui.sameLine();
        ImGui.setNextItemWidth(150);
        if (ImGui.combo("##shadingMode", editorRepository.shadingModelOption, SHADING_MODE_OPTIONS)) {
            engineRepository.debugShadingModel = DebugShadingModel.values()[editorRepository.shadingModelOption.get()];
        }
    }

}
