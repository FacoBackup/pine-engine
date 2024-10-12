package com.pine.panels.component.impl;

import com.pine.inspection.FieldDTO;
import com.pine.panels.component.AbstractFormField;
import imgui.ImGui;

import java.util.function.BiConsumer;

public class FloatField extends AbstractFormField {
    private final float[] values = new float[1];

    public FloatField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        var cast = (Float) dto.getValue();
        values[0] = cast;
    }

    @Override
    public void render() {
        ImGui.text(dto.getLabel());
        if(ImGui.dragFloat(imguiId, values, .01f, dto.getMin(), dto.getMax())){
            changerHandler.accept(dto, values[0]);
        }
    }
}
