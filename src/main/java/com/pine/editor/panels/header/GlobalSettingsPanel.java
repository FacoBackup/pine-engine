package com.pine.editor.panels.header;

import com.pine.common.Icons;
import com.pine.common.injection.PInject;
import com.pine.editor.core.AbstractView;
import com.pine.editor.core.UIUtil;
import com.pine.editor.repository.EditorMode;
import com.pine.editor.repository.EditorRepository;
import com.pine.engine.component.ComponentType;
import com.pine.engine.repository.CameraRepository;
import com.pine.engine.repository.EngineRepository;
import com.pine.engine.repository.ShadingMode;
import com.pine.engine.service.rendering.RequestProcessingService;
import com.pine.engine.service.request.AddComponentRequest;
import com.pine.engine.service.request.AddEntityRequest;
import imgui.ImGui;
import imgui.type.ImInt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.pine.common.Icons.ONLY_ICON_BUTTON_SIZE;

public class GlobalSettingsPanel extends AbstractView {
    private static final String[] SHADING_MODE_OPTIONS = ShadingMode.getLabels();

    private final float[] cameraSensitivity = new float[1];
    private final ImInt editorMode = new ImInt(0);

    @PInject
    public EngineRepository engineRepository;

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public RequestProcessingService requestProcessingService;

    private final List<String> types = new ArrayList<>();

    @Override
    public void onInitialize() {
        types.add(Icons.add + " Add entity");
        types.addAll(ComponentType.getSoleTypes().stream().map(a -> a.getIcon() + a.getTitle()).toList());
    }

    @Override
    public void render() {
        editorMode.set(editorRepository.editorMode.index);
        ImGui.setNextItemWidth(150);
        if (ImGui.combo(imguiId, editorMode, EditorMode.getOptions())) {
            editorRepository.editorMode = EditorMode.valueOfIndex(editorMode.get());
        }

        ImGui.sameLine();
        ImGui.setNextItemWidth(150);
        if (ImGui.beginCombo(imguiId + "entities", types.getFirst())) {
            for (int i = 1; i < types.size(); i++) {
                String type = types.get(i);
                if (ImGui.selectable(type)) {
                    ComponentType entityComponent = ComponentType.getSoleTypes().get(i - 1);
                    requestProcessingService.addRequest(new AddEntityRequest(List.of(entityComponent)));
                }
            }
            ImGui.endCombo();
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
        if (ImGui.dragFloat("##speedCamera", cameraSensitivity, .1f, .1f)) {
            cameraRepository.movementSensitivity = Math.max(0, cameraSensitivity[0]);
        }
    }

    private void shadingMode() {
        UIUtil.dynamicSpacing(405);

        ImGui.text("Shading");

        ImGui.sameLine();
        if (UIUtil.renderOption(Icons.grid_on + "##world", editorRepository.gridOverlayObjects, true, editorRepository.accent)) {
            editorRepository.gridOverlayObjects = !editorRepository.gridOverlayObjects;
        }

        ImGui.sameLine();
        if (UIUtil.renderOption(Icons.details + "Wireframe##wireframeShading", engineRepository.shadingMode == ShadingMode.WIREFRAME, false, editorRepository.accent)) {
            engineRepository.shadingMode = ShadingMode.WIREFRAME;
            editorRepository.shadingModelOption.set(ShadingMode.WIREFRAME.getIndex());
        }

        ImGui.sameLine();
        if (UIUtil.renderOption(Icons.palette + "Random##randomShading", engineRepository.shadingMode == ShadingMode.RANDOM, false, editorRepository.accent)) {
            engineRepository.shadingMode = ShadingMode.RANDOM;
            editorRepository.shadingModelOption.set(ShadingMode.RANDOM.getIndex());
        }

        ImGui.sameLine();
        ImGui.setNextItemWidth(150);
        if (ImGui.combo("##shadingMode", editorRepository.shadingModelOption, SHADING_MODE_OPTIONS)) {
            engineRepository.shadingMode = ShadingMode.values()[editorRepository.shadingModelOption.get()];
        }
    }

}
