package com.pine.panels.header;

import com.pine.core.view.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.EditorRepository;
import com.pine.repository.EngineSettingsRepository;
import com.pine.service.svo.VoxelService;
import com.pine.theme.Icons;
import com.pine.repository.DebugShadingModel;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiStyleVar;

import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class ViewportHeaderPanel extends AbstractView {
    private static final ImVec2 SPACING = new ImVec2(0, 0);
    private static final String[] SNAP_ROTATE_OPTIONS = new String[]{"5", "10", "15", "30", "45"};
    private static final String[] SNAP_TRANSLATE_OPTIONS = new String[]{"0.5", "1", "2", "5", "10"};
    private static final String[] SNAP_SCALE_OPTIONS = new String[]{"0.5", "1", "2", "5", "10"};
    private static final String[] GIZMO_MODE_OPTIONS = new String[]{Icons.language + " World", Icons.place + " Local"};
    private static final String[] SHADING_MODE_OPTIONS = DebugShadingModel.getLabels();
    private static final ImVec2 MEDIUM_SPACING = new ImVec2(5, 0);
    private static final ImVec2 LARGE_SPACING = new ImVec2(40, 0);

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public EngineSettingsRepository engineSettingsRepository;

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public VoxelService voxelService;

    @Override
    public void render() {
        if (ImGui.button(Icons.apps + "Repackage scene voxels##vp")) {
            voxelService.buildFromScratch();
        }

        largeSpacing();

        gizmoMode();

        gizmoSelection();

        gizmoGrid();

        cameraMode();

        shadingMode();

        ImGui.sameLine();
        ImGui.dummy(1, 0);
    }

    private void cameraMode() {
        largeSpacing();
        ImGui.text("Camera");

        if (cameraRepository.currentCamera.orbitalMode) {
            ImGui.sameLine();
            if (ImGui.button(Icons.center_focus_strong + "##centerCamera", ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE)) {
                cameraRepository.currentCamera.orbitCenter.zero();
                cameraRepository.currentCamera.registerChange();
            }
        }

        ImGui.sameLine();
        if (ImGui.button(cameraRepository.currentCamera.orbitalMode ? "Orbital " + Icons.trip_origin : "Free " + Icons.outbound + "##cameraMode")) {
            cameraRepository.currentCamera.orbitalMode = !cameraRepository.currentCamera.orbitalMode;
        }
        ImGui.sameLine();
    }

    private void shadingMode() {
        ImGui.sameLine();
        ImGui.dummy(ImGui.getContentRegionAvailX() - 382, 0);
        ImGui.sameLine();

        ImGui.text("Shading");

        ImGui.sameLine();
        if (renderOption(Icons.grid_on + "Wireframe##wireframeShading", engineSettingsRepository.debugShadingModel == DebugShadingModel.WIREFRAME, false)) {
            engineSettingsRepository.debugShadingModel = DebugShadingModel.WIREFRAME;
            editorRepository.shadingModelOption.set(DebugShadingModel.WIREFRAME.getIndex());
        }

        ImGui.sameLine();
        if (renderOption(Icons.palette + "Random##randomShading", engineSettingsRepository.debugShadingModel == DebugShadingModel.RANDOM, false)) {
            engineSettingsRepository.debugShadingModel = DebugShadingModel.RANDOM;
            editorRepository.shadingModelOption.set(DebugShadingModel.RANDOM.getIndex());
        }

        ImGui.sameLine();
        ImGui.setNextItemWidth(150);
        if (ImGui.combo("##shadingMode", editorRepository.shadingModelOption, SHADING_MODE_OPTIONS)) {
            engineSettingsRepository.debugShadingModel = DebugShadingModel.values()[editorRepository.shadingModelOption.get()];
        }
    }

    private void gizmoGrid() {
        largeSpacing();
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, SPACING);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemInnerSpacing, SPACING);
        renderSnapTranslate();
        ImGui.popStyleVar(2);

        spacing();
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, SPACING);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemInnerSpacing, SPACING);
        renderSnapRotate();
        ImGui.popStyleVar(2);

        spacing();
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, SPACING);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemInnerSpacing, SPACING);
        renderSnapScale();
        ImGui.popStyleVar(2);
    }

    private void gizmoMode() {
        if (editorRepository.gizmoOperation != Operation.SCALE) {
            ImGui.setNextItemWidth(85);
            if (ImGui.combo("##gizmoMode", editorRepository.gizmoModeOption, GIZMO_MODE_OPTIONS)) {
                editorRepository.gizmoMode = editorRepository.gizmoModeOption.get() == 1f ? Mode.LOCAL : Mode.WORLD;
            }
            largeSpacing();
        }
    }

    private void gizmoSelection() {
        if (renderOption(Icons.control_camera + " Translate", editorRepository.gizmoOperation == Operation.TRANSLATE, false)) {
            editorRepository.gizmoOperation = Operation.TRANSLATE;
        }
        ImGui.sameLine();
        if (renderOption(Icons.crop_rotate + " Rotate", editorRepository.gizmoOperation == Operation.ROTATE, false)) {
            editorRepository.gizmoOperation = Operation.ROTATE;
        }
        ImGui.sameLine();
        if (renderOption(Icons.expand + " Scale", editorRepository.gizmoOperation == Operation.SCALE, false)) {
            editorRepository.gizmoOperation = Operation.SCALE;
        }
    }

    public static void largeSpacing() {
        ImGui.sameLine();
        ImGui.dummy(LARGE_SPACING);
        ImGui.sameLine();
    }

    public static void spacing() {
        ImGui.sameLine();
        ImGui.dummy(MEDIUM_SPACING);
        ImGui.sameLine();
    }

    private void renderSnapTranslate() {
        if (renderOption(Icons.control_camera, editorRepository.gizmoUseSnapTranslate, true)) {
            editorRepository.gizmoUseSnapTranslate = !editorRepository.gizmoUseSnapTranslate;
        }
        ImGui.sameLine();
        ImGui.setNextItemWidth(50);
        if (ImGui.combo("##translateSnapAngle", editorRepository.gizmoSnapTranslateOption, SNAP_TRANSLATE_OPTIONS)) {
            float data = Float.parseFloat(SNAP_TRANSLATE_OPTIONS[editorRepository.gizmoSnapTranslateOption.get()]);
            editorRepository.gizmoSnapTranslate[0] = data;
            editorRepository.gizmoSnapTranslate[1] = data;
            editorRepository.gizmoSnapTranslate[2] = data;
        }
    }

    private void renderSnapRotate() {
        if (renderOption(Icons.i360, editorRepository.gizmoUseSnapRotate, true)) {
            editorRepository.gizmoUseSnapRotate = !editorRepository.gizmoUseSnapRotate;
        }
        ImGui.sameLine();
        ImGui.setNextItemWidth(50);
        if (ImGui.combo("##rotateSnapAngle", editorRepository.gizmoSnapRotateOption, SNAP_ROTATE_OPTIONS)) {
            float data = Float.parseFloat(SNAP_ROTATE_OPTIONS[editorRepository.gizmoSnapRotateOption.get()]);
            editorRepository.gizmoSnapRotate.set(data);
        }
    }

    private void renderSnapScale() {
        if (renderOption(Icons.expand, editorRepository.gizmoUseSnapScale, true)) {
            editorRepository.gizmoUseSnapScale = !editorRepository.gizmoUseSnapScale;
        }
        ImGui.sameLine();
        ImGui.setNextItemWidth(50);
        if (ImGui.combo("##scaleSnapAngle", editorRepository.gizmoSnapScaleOption, SNAP_SCALE_OPTIONS, ImGuiComboFlags.NoArrowButton)) {
            float data = Float.parseFloat(SNAP_SCALE_OPTIONS[editorRepository.gizmoSnapScaleOption.get()]);
            editorRepository.gizmoSnapScale.set(data);
        }
    }

    private boolean renderOption(String label, boolean selected, boolean fixedSize) {
        int popStyle = 0;
        if (selected) {
            ImGui.pushStyleColor(ImGuiCol.Button, editorRepository.accent);
            popStyle++;
        }
        boolean value;
        if (fixedSize) {
            value = ImGui.button(label, ONLY_ICON_BUTTON_SIZE, ONLY_ICON_BUTTON_SIZE);
        } else {
            value = ImGui.button(label);
        }
        ImGui.popStyleColor(popStyle);
        return value;
    }
}
