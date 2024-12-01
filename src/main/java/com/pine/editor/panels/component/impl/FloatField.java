package com.pine.editor.panels.component.impl;

import com.pine.common.inspection.FieldDTO;
import com.pine.editor.panels.component.AbstractFormField;
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
        values[0] = (Float) dto.getValue();

        if (dto.isDisabled()) {
            ImGui.text(dto.getLabel() + ": ");
            ImGui.textDisabled(String.valueOf(values[0]));
        } else {
            ImGui.text(dto.getLabel());
            if (ImGui.dragFloat(imguiId, values, .01f, dto.getMin(), dto.getMax())) {
                changeHandler.accept(dto, values[0]);
            }
        }
    }
}
