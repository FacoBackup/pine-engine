package com.pine.panels.component.impl;

import com.pine.inspection.FieldDTO;
import com.pine.panels.component.AbstractFormField;
import imgui.ImGui;
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
    public void render() {
        values[0] = valVec.x;
        values[1] = valVec.y;
        values[2] = valVec.z;
        values[3] = valVec.w;

        ImGui.text(dto.getLabel());
        if (ImGui.dragFloat4(imguiId, values, .01f, dto.getMin(), dto.getMax())) {
            changerHandler.accept(dto, values);
        }
    }
}
