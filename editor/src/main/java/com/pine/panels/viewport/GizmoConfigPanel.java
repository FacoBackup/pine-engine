package com.pine.panels.viewport;

import com.pine.PInject;
import com.pine.repository.EditorSettingsRepository;
import com.pine.view.AbstractView;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiKey;

public class GizmoConfigPanel extends AbstractView {
    public static final int GIZMO_PANEL_SIZE = 30;
    private final ImGuiIO io;
    @PInject
    public EditorSettingsRepository editorSettingsRepository;

    public GizmoConfigPanel() {
        io = ImGui.getIO();
    }

    @Override
    public void renderInternal() {
        int framerate = (int) io.getFramerate();
        ImGui.text(1000 / framerate + "ms | " + framerate + "fps");
        ImGui.sameLine();
        if (ImGui.isKeyPressed(ImGuiKey.T))
            editorSettingsRepository.gizmoOperation = Operation.TRANSLATE;
        if (ImGui.isKeyPressed(ImGuiKey.R))
            editorSettingsRepository.gizmoOperation = Operation.ROTATE;
        if (ImGui.isKeyPressed(ImGuiKey.Y))
            editorSettingsRepository.gizmoOperation = Operation.SCALE;

        if (ImGui.radioButton("Translate", editorSettingsRepository.gizmoOperation == Operation.TRANSLATE))
            editorSettingsRepository.gizmoOperation = Operation.TRANSLATE;
        ImGui.sameLine();
        if (ImGui.radioButton("Rotate", editorSettingsRepository.gizmoOperation == Operation.ROTATE))
            editorSettingsRepository.gizmoOperation = Operation.ROTATE;
        ImGui.sameLine();
        if (ImGui.radioButton("Scale", editorSettingsRepository.gizmoOperation == Operation.SCALE))
            editorSettingsRepository.gizmoOperation = Operation.SCALE;

        if (editorSettingsRepository.gizmoOperation != Operation.SCALE) {
            ImGui.sameLine();
            if (ImGui.radioButton("Local", editorSettingsRepository.gizmoMode == Mode.LOCAL))
                editorSettingsRepository.gizmoMode = Mode.LOCAL;
            ImGui.sameLine();
            if (ImGui.radioButton("World", editorSettingsRepository.gizmoMode == Mode.WORLD))
                editorSettingsRepository.gizmoMode = Mode.WORLD;
        }

        if (ImGui.isKeyPressed(ImGuiKey.F))
            editorSettingsRepository.gizmoUseSnap = !editorSettingsRepository.gizmoUseSnap;

        ImGui.sameLine();
        ImGui.checkbox("Snap" + imguiId, editorSettingsRepository.gizmoUseSnap);
        ImGui.sameLine();

        switch (editorSettingsRepository.gizmoOperation) {
            case Operation.TRANSLATE -> ImGui.inputFloat3("Snap", editorSettingsRepository.gizmoSnapTranslate);
            case Operation.ROTATE -> ImGui.inputFloat("Angle Snap", editorSettingsRepository.gizmoSnapRotate);
            case Operation.SCALE -> ImGui.inputFloat("Scale Snap", editorSettingsRepository.gizmoSnapScale);
        }
    }
}
