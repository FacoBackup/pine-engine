package com.pine.panels.viewport;

import com.pine.PInject;
import com.pine.repository.EditorSettingsRepository;
import com.pine.theme.Icons;
import com.pine.tools.types.DebugShadingModel;
import com.pine.view.AbstractView;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;

import java.util.Arrays;

import static com.pine.dock.DockWrapperPanel.FRAME_SIZE;
import static com.pine.theme.Icons.ONLY_ICON_BUTTON_SIZE;
import static com.pine.theme.Icons.spa;

public class GizmoConfigPanel extends AbstractView {
    private static final ImVec2 SPACING = new ImVec2(0, 0);
    private static final ImVec2 PADDING = new ImVec2(4, 4);
    private static final String[] SNAP_ROTATE_OPTIONS = new String[]{"5", "10", "15", "30", "45"};
    private static final String[] SNAP_TRANSLATE_OPTIONS = new String[]{"0.5", "1", "2", "5", "10"};
    private static final String[] SNAP_SCALE_OPTIONS = new String[]{"0.5", "1", "2", "5", "10"};
    private static final String[] GIZMO_MODE_OPTIONS = new String[]{Icons.language + " World", Icons.place + " Local"};
    private static final ImBoolean OPEN = new ImBoolean(true);
    private static final int FLAGS = ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar |  ImGuiWindowFlags.NoMove;
    private static final String[] SHADING_MODE_OPTIONS = DebugShadingModel.getLabels();
    private static final ImVec2 MEDIUM_SPACING = new ImVec2(5, 0);
    private static final ImVec2 LARGE_SPACING = new ImVec2(40, 0);

    @PInject
    public EditorSettingsRepository settingsRepository;

    private final ImGuiIO io;
    private final ImVec2 size;
    private final ImVec2 position;

    public GizmoConfigPanel(ImVec2 position, ImVec2 size) {
        io = ImGui.getIO();
        this.size = size;
        this.position = position;
    }

    @Override
    public void renderInternal() {
        ImGui.setNextWindowSize(size.x, 32);
        ImGui.setNextWindowPos(position.x, position.y + FRAME_SIZE);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, PADDING);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);
        ImGui.setNextWindowBgAlpha(0);
        ImGui.begin("##gizmoSettings", OPEN, FLAGS);
        ImGui.popStyleVar(2);
        hotKeys();

        gizmoMode();

        gizmoSelection();

        gizmoGrid();

        shadingMode();

        ImGui.end();
    }

    private void shadingMode() {
        ImGui.sameLine();
        ImGui.dummy(ImGui.getContentRegionAvailX() - 230, 0);
        ImGui.sameLine();

        ImGui.text(Icons.texture);
        ImGui.sameLine();
        ImGui.setNextItemWidth(200);
        if(ImGui.combo("##shadingMode", settingsRepository.shadingModelOption, SHADING_MODE_OPTIONS)){
            settingsRepository.debugShadingModel = DebugShadingModel.values()[settingsRepository.shadingModelOption.get()];
        }
    }

    private void framerate() {
        largeSpacing();
        int framerate = Math.max(1, (int) io.getFramerate());
        ImGui.text(1000 / framerate + "ms | " + framerate + "fps");
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
        if (settingsRepository.gizmoOperation != Operation.SCALE) {
            ImGui.setNextItemWidth(85);
            if (ImGui.combo("##gizmoMode", settingsRepository.gizmoModeOption, GIZMO_MODE_OPTIONS)) {
                settingsRepository.gizmoMode = settingsRepository.gizmoModeOption.get() == 1f ? Mode.LOCAL : Mode.WORLD;
            }
            largeSpacing();
        }
    }

    private void gizmoSelection() {
        if (renderOption(Icons.control_camera + " Translate", settingsRepository.gizmoOperation == Operation.TRANSLATE, false)) {
            settingsRepository.gizmoOperation = Operation.TRANSLATE;
        }
        ImGui.sameLine();
        if (renderOption(Icons.crop_rotate + " Rotate", settingsRepository.gizmoOperation == Operation.ROTATE, false)) {
            settingsRepository.gizmoOperation = Operation.ROTATE;
        }
        ImGui.sameLine();
        if (renderOption(Icons.expand + " Scale", settingsRepository.gizmoOperation == Operation.SCALE, false)) {
            settingsRepository.gizmoOperation = Operation.SCALE;
        }
    }

    private void hotKeys() {
        if (ImGui.isKeyPressed(ImGuiKey.T))
            settingsRepository.gizmoOperation = Operation.TRANSLATE;
        if (ImGui.isKeyPressed(ImGuiKey.R))
            settingsRepository.gizmoOperation = Operation.ROTATE;
        if (ImGui.isKeyPressed(ImGuiKey.Y))
            settingsRepository.gizmoOperation = Operation.SCALE;
    }

    private void largeSpacing() {
        ImGui.sameLine();
        ImGui.dummy(LARGE_SPACING);
        ImGui.sameLine();
    }

    private static void spacing() {
        ImGui.sameLine();
        ImGui.dummy(MEDIUM_SPACING);
        ImGui.sameLine();
    }

    private void renderSnapTranslate() {
        if (renderOption(Icons.control_camera, settingsRepository.gizmoUseSnapTranslate, true)) {
            settingsRepository.gizmoUseSnapTranslate = !settingsRepository.gizmoUseSnapTranslate;
        }
        ImGui.sameLine();
        ImGui.setNextItemWidth(50);
        if (ImGui.combo("##translateSnapAngle", settingsRepository.gizmoSnapTranslateOption, SNAP_TRANSLATE_OPTIONS)) {
            float data = Float.parseFloat(SNAP_TRANSLATE_OPTIONS[settingsRepository.gizmoSnapTranslateOption.get()]);
            settingsRepository.gizmoSnapTranslate[0] = data;
            settingsRepository.gizmoSnapTranslate[1] = data;
            settingsRepository.gizmoSnapTranslate[2] = data;
        }
    }

    private void renderSnapRotate() {
        if (renderOption(Icons.i360, settingsRepository.gizmoUseSnapRotate, true)) {
            settingsRepository.gizmoUseSnapRotate = !settingsRepository.gizmoUseSnapRotate;
        }
        ImGui.sameLine();
        ImGui.setNextItemWidth(50);
        if (ImGui.combo("##rotateSnapAngle", settingsRepository.gizmoSnapRotateOption, SNAP_ROTATE_OPTIONS)) {
            float data = Float.parseFloat(SNAP_ROTATE_OPTIONS[settingsRepository.gizmoSnapRotateOption.get()]);
            settingsRepository.gizmoSnapRotate.set(data);
        }
    }

    private void renderSnapScale() {
        if (renderOption(Icons.expand, settingsRepository.gizmoUseSnapScale, true)) {
            settingsRepository.gizmoUseSnapScale = !settingsRepository.gizmoUseSnapScale;
        }
        ImGui.sameLine();
        ImGui.setNextItemWidth(50);
        if (ImGui.combo("##scaleSnapAngle", settingsRepository.gizmoSnapScaleOption, SNAP_SCALE_OPTIONS, ImGuiComboFlags.NoArrowButton)) {
            float data = Float.parseFloat(SNAP_SCALE_OPTIONS[settingsRepository.gizmoSnapScaleOption.get()]);
            settingsRepository.gizmoSnapScale.set(data);
        }
    }

    private boolean renderOption(String label, boolean selected, boolean fixedSize) {
        int popStyle = 0;
        if (selected) {
            ImGui.pushStyleColor(ImGuiCol.Button, settingsRepository.accentColor);
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
