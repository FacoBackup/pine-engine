package com.pine.panels.viewport;

import com.pine.Engine;
import com.pine.PInject;
import com.pine.component.TransformationComponent;
import com.pine.repository.CameraRepository;
import com.pine.repository.EditorRepository;
import com.pine.repository.EntitySelectionRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.service.resource.ResourceService;
import com.pine.service.world.WorldService;
import com.pine.ui.panel.AbstractPanel;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

public class GizmoConfigPanel extends AbstractPanel {
    @PInject
    public EditorRepository editorRepository;

    private final ImVec2 size;
    private final ImVec2 position;

    public GizmoConfigPanel(ImVec2 position, ImVec2 size) {
        this.size = size;
        this.position = position;
    }

    @Override
    public void renderInternal() {
        ImGui.setNextWindowPos(position);
        ImGui.setNextWindowSize(size.x, size.y * .2f);
        ImGui.begin("##gizmoOptions", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize);
        if (ImGui.isKeyPressed(ImGuiKey.T))
            editorRepository.gizmoOperation = Operation.TRANSLATE;
        if (ImGui.isKeyPressed(ImGuiKey.R))
            editorRepository.gizmoOperation = Operation.ROTATE;
        if (ImGui.isKeyPressed(ImGuiKey.Y))
            editorRepository.gizmoOperation = Operation.SCALE;

        if (ImGui.radioButton("Translate", editorRepository.gizmoOperation == Operation.TRANSLATE))
            editorRepository.gizmoOperation = Operation.TRANSLATE;
        ImGui.sameLine();
        if (ImGui.radioButton("Rotate", editorRepository.gizmoOperation == Operation.ROTATE))
            editorRepository.gizmoOperation = Operation.ROTATE;
        ImGui.sameLine();
        if (ImGui.radioButton("Scale", editorRepository.gizmoOperation == Operation.SCALE))
            editorRepository.gizmoOperation = Operation.SCALE;

        if (editorRepository.gizmoOperation != Operation.SCALE) {
            ImGui.sameLine();
            if (ImGui.radioButton("Local", editorRepository.gizmoMode == Mode.LOCAL))
                editorRepository.gizmoMode = Mode.LOCAL;
            ImGui.sameLine();
            if (ImGui.radioButton("World", editorRepository.gizmoMode == Mode.WORLD))
                editorRepository.gizmoMode = Mode.WORLD;
        }

        if (ImGui.isKeyPressed(ImGuiKey.F))
            editorRepository.gizmoUseSnap = !editorRepository.gizmoUseSnap;

        ImGui.sameLine();
        ImGui.checkbox("Snap" + internalId, editorRepository.gizmoUseSnap);
        ImGui.sameLine();

        switch (editorRepository.gizmoOperation) {
            case Operation.TRANSLATE -> ImGui.inputFloat3("Snap", editorRepository.gizmoSnapTranslate);
            case Operation.ROTATE -> ImGui.inputFloat("Angle Snap", editorRepository.gizmoSnapRotate);
            case Operation.SCALE -> ImGui.inputFloat("Scale Snap", editorRepository.gizmoSnapScale);
        }

        ImGui.end();
    }

}
