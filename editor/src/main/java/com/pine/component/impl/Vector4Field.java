package com.pine.component.impl;

import com.pine.component.AbstractFormField;
import com.pine.inspection.FieldDTO;
import imgui.ImGui;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.BiConsumer;

public class Vector4Field extends AbstractFormField {
    private final float[] values = new float[4];
    private final Vector4f valVec;

    public Vector4Field(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        valVec = (Vector4f) dto.getValue();
    }

    @Override
    public void renderInternal() {
        values[0] = valVec.x;
        values[1] = valVec.y;
        values[2] = valVec.z;
        values[3] = valVec.w;

        ImGui.text(dto.getLabel());
        if (ImGui.dragFloat4(internalId, values, .01f, dto.getMin(), dto.getMax())) {
            changerHandler.accept(dto, values);
        }
    }
}
