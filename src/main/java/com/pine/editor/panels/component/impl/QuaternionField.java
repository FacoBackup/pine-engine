package com.pine.editor.panels.component.impl;

import com.pine.common.inspection.FieldDTO;
import com.pine.editor.panels.component.AbstractFormField;
import imgui.ImGui;
import org.joml.Quaternionf;

import java.util.function.BiConsumer;

public class QuaternionField extends AbstractFormField {
    private final float[] values = new float[4];
    private final Quaternionf valQuat;

    public QuaternionField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        valQuat = (Quaternionf) dto.getValue();
    }

    @Override
    public void render() {
        values[0] = valQuat.x;
        values[1] = valQuat.y;
        values[2] = valQuat.z;
        values[3] = valQuat.w;
        ImGui.text(dto.getLabel());

        if (dto.isDisabled()) {
            ImGui.text("X: " + values[0]);
            ImGui.text("Y: " + values[1]);
            ImGui.text("Z: " + values[2]);
            ImGui.text("W: " + values[3]);
        } else {
            if (ImGui.dragFloat4(imguiId, values, .01f, dto.getMin(), dto.getMax())) {
                changeHandler.accept(dto, values);
            }
        }
    }
}