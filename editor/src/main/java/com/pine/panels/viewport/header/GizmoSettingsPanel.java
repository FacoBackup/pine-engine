package com.pine.panels.viewport.header;

import com.pine.core.UIUtil;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiStyleVar;

public class GizmoSettingsPanel extends AbstractViewportSettingsPanel {
    private static final ImVec2 SPACING = new ImVec2(0, 0);
    private static final String[] SNAP_ROTATE_OPTIONS = new String[]{"5", "10", "15", "30", "45"};
    private static final String[] SNAP_TRANSLATE_OPTIONS = new String[]{"0.5", "1", "2", "5", "10"};
    private static final String[] SNAP_SCALE_OPTIONS = new String[]{"0.5", "1", "2", "5", "10"};

    @Override
    public void render() {
        gizmoMode();
        gizmoSelection();
        gizmoGrid();
    }

    private void gizmoGrid() {
        UIUtil.largeSpacing();
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, SPACING);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemInnerSpacing, SPACING);
        renderSnapTranslate();
        ImGui.popStyleVar(2);

        UIUtil.spacing();
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, SPACING);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemInnerSpacing, SPACING);
        renderSnapRotate();
        ImGui.popStyleVar(2);

        UIUtil.spacing();
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, SPACING);
        ImGui.pushStyleVar(ImGuiStyleVar.ItemInnerSpacing, SPACING);
        renderSnapScale();
        ImGui.popStyleVar(2);
    }

    private void gizmoMode() {
        ImGui.setNextItemWidth(85);
        if (ImGui.button(editorRepository.gizmoMode == Mode.WORLD ? Icons.language + "World" : Icons.place + "Local")) {
            editorRepository.gizmoMode = editorRepository.gizmoMode == Mode.LOCAL ? Mode.WORLD : Mode.LOCAL;
        }
        UIUtil.largeSpacing();
    }

    private void gizmoSelection() {
        if (UIUtil.renderOption(Icons.control_camera + " Translate", editorRepository.gizmoType == Operation.TRANSLATE, false, editorRepository.accent)) {
            editorRepository.gizmoType = Operation.TRANSLATE;
        }
        ImGui.sameLine();
        if (UIUtil.renderOption(Icons.crop_rotate + " Rotate", editorRepository.gizmoType == Operation.ROTATE, false, editorRepository.accent)) {
            editorRepository.gizmoType = Operation.ROTATE;
        }
        ImGui.sameLine();
        if (UIUtil.renderOption(Icons.expand + " Scale", editorRepository.gizmoType == Operation.SCALE, false, editorRepository.accent)) {
            editorRepository.gizmoType = Operation.SCALE;
        }
    }

    private void renderSnapTranslate() {
        if (UIUtil.renderOption(Icons.control_camera, editorRepository.gizmoUseSnapTranslate, true, editorRepository.accent)) {
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
        if (UIUtil.renderOption(Icons.i360, editorRepository.gizmoUseSnapRotate, true, editorRepository.accent)) {
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
        if (UIUtil.renderOption(Icons.expand, editorRepository.gizmoUseSnapScale, true, editorRepository.accent)) {
            editorRepository.gizmoUseSnapScale = !editorRepository.gizmoUseSnapScale;
        }
        ImGui.sameLine();
        ImGui.setNextItemWidth(50);
        if (ImGui.combo("##scaleSnapAngle", editorRepository.gizmoSnapScaleOption, SNAP_SCALE_OPTIONS, ImGuiComboFlags.NoArrowButton)) {
            float data = Float.parseFloat(SNAP_SCALE_OPTIONS[editorRepository.gizmoSnapScaleOption.get()]);
            editorRepository.gizmoSnapScale.set(data);
        }
    }
}
