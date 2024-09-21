package com.pine.app.component.impl;

import com.pine.app.component.AbstractFormField;
import com.pine.inspection.FieldDTO;
import imgui.ImGui;

import java.util.function.BiConsumer;

public class VectorField extends AbstractFormField {
    private final float[] values = new float[4];

    public VectorField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
    }

    @Override
    public void renderInternal() {
        switch (dto.getType()) {
            case VECTOR2 -> ImGui.dragFloat2(dto.getLabel(), values, .01f, dto.getMin(), dto.getMax());
            case VECTOR3 -> ImGui.dragFloat3(dto.getLabel(), values, .01f, dto.getMin(), dto.getMax());
            case VECTOR4 -> ImGui.dragFloat4(dto.getLabel(), values, .01f, dto.getMin(), dto.getMax());
        }
    }
}
