package com.pine.editor.panels.component.impl;

import com.pine.common.inspection.FieldDTO;
import com.pine.editor.panels.component.AbstractFormField;
import imgui.ImGui;

import java.util.function.BiConsumer;

public class IntField extends AbstractFormField {
    private final int[] values = new int[1];

    public IntField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        var cast = (Integer) dto.getValue();
        values[0] = cast;
    }

    @Override
    public void render() {
        values[0] = (Integer) dto.getValue();

        if (dto.isDisabled()) {
            ImGui.text(dto.getLabel() + ": ");
            ImGui.textDisabled(String.valueOf(values[0]));
        } else {
            ImGui.text(dto.getLabel());
            if (ImGui.dragInt(imguiId, values, 1, dto.getMin(), dto.getMax())) {
                changeHandler.accept(dto, values[0]);
            }
        }
    }
}