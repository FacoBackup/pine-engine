package com.pine.panels.viewport;

import com.pine.core.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.*;
import com.pine.theme.Icons;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;

import static com.pine.core.AbstractWindow.*;
import static com.pine.core.dock.DockSpacePanel.FRAME_SIZE;
import static com.pine.panels.header.GlobalSettingsPanel.renderOption;
import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;

public class ViewportHeaderPanel extends AbstractView {
    private static final int FLAGS = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse;
    private static final ImVec2 SPACING = new ImVec2(0, 0);
    private static final String[] SNAP_ROTATE_OPTIONS = new String[]{"5", "10", "15", "30", "45"};
    private static final String[] SNAP_TRANSLATE_OPTIONS = new String[]{"0.5", "1", "2", "5", "10"};
    private static final String[] SNAP_SCALE_OPTIONS = new String[]{"0.5", "1", "2", "5", "10"};
    private static final ImVec2 MEDIUM_SPACING = new ImVec2(5, 0);
    private static final ImVec2 LARGE_SPACING = new ImVec2(40, 0);
    private static final ImVec2 PADDING = new ImVec2(4, 4);
    private static final float HEIGHT = 35;
    private final ImVec2 position = new ImVec2(0, 0);
    private final ImVec2 size = new ImVec2(0, 0);

    @PInject
    public EditorRepository editorRepository;

    @PInject
    public CameraRepository cameraRepository;

    @Override
    public void render() {
        position.x = ImGui.getWindowPosX();
        position.y = ImGui.getWindowPosY() + FRAME_SIZE;

        size.x = ImGui.getWindowSizeX();
        size.y = HEIGHT;

        ImGui.setNextWindowPos(position);
        ImGui.setNextWindowSize(size);
        ImGui.setNextWindowBgAlpha(.1f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, PADDING);
        ImGui.begin(imguiId, OPEN, FLAGS);
        ImGui.popStyleVar();

        renderContent();
        ImGui.end();
    }

    private void renderContent() {
        gizmoMode();
        gizmoSelection();
        gizmoGrid();
        cameraMode();
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
        ImGui.dummy(ImGui.getContentRegionAvailX() - 75, 0);
        ImGui.sameLine();
        if (ImGui.button(cameraRepository.currentCamera.orbitalMode ? "Orbital " + Icons.trip_origin : "Free " + Icons.outbound + "##cameraMode")) {
            cameraRepository.currentCamera.orbitalMode = !cameraRepository.currentCamera.orbitalMode;
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
        ImGui.setNextItemWidth(85);
        if (ImGui.button(editorRepository.gizmoMode == Mode.WORLD ? Icons.language + "World" : Icons.place + "Local")) {
            editorRepository.gizmoMode = editorRepository.gizmoMode == Mode.LOCAL ? Mode.WORLD : Mode.LOCAL;
        }
        largeSpacing();
    }

    private void gizmoSelection() {
        if (renderOption(Icons.control_camera + " Translate", editorRepository.gizmoType == Operation.TRANSLATE, false, editorRepository.accent)) {
            editorRepository.gizmoType = Operation.TRANSLATE;
        }
        ImGui.sameLine();
        if (renderOption(Icons.crop_rotate + " Rotate", editorRepository.gizmoType == Operation.ROTATE, false, editorRepository.accent)) {
            editorRepository.gizmoType = Operation.ROTATE;
        }
        ImGui.sameLine();
        if (renderOption(Icons.expand + " Scale", editorRepository.gizmoType == Operation.SCALE, false, editorRepository.accent)) {
            editorRepository.gizmoType = Operation.SCALE;
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
        if (renderOption(Icons.control_camera, editorRepository.gizmoUseSnapTranslate, true, editorRepository.accent)) {
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
        if (renderOption(Icons.i360, editorRepository.gizmoUseSnapRotate, true, editorRepository.accent)) {
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
        if (renderOption(Icons.expand, editorRepository.gizmoUseSnapScale, true, editorRepository.accent)) {
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
