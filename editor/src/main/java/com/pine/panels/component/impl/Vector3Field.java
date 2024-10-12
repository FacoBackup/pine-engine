package com.pine.panels.component.impl;

import com.pine.inspection.FieldDTO;
import com.pine.panels.component.AbstractFormField;
import imgui.ImGui;
import org.joml.Vector3f;

import java.util.function.BiConsumer;

public class Vector3Field extends AbstractFormField {
    private final float[] values = new float[3];
    private final Vector3f valVec3;

    public Vector3Field(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        valVec3 = (Vector3f) dto.getValue();
    }

    @Override
    public void render() {

        values[0] = valVec3.x;
        values[1] = valVec3.y;
        values[2] = valVec3.z;

        ImGui.text(dto.getLabel());
        if (ImGui.dragFloat3(imguiId, values, .01f, dto.getMin(), dto.getMax())) {
            changerHandler.accept(dto, values);
        }
    }
}
