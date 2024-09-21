package com.pine.app.component.impl;

import com.pine.app.component.AbstractFormField;
import com.pine.inspection.FieldDTO;
import imgui.ImGui;

import java.util.function.BiConsumer;

public class FloatField extends AbstractFormField {
    private final float[] values = new float[0];

    public FloatField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
    }

    @Override
    public void renderInternal() {
        if(ImGui.dragFloat(dto.getLabel(), values, .01f, dto.getMin(), dto.getMax())){
            changerHandler.accept(dto, values[0]);
        }
    }
}
