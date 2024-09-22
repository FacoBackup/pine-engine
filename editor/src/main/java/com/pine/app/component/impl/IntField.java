package com.pine.app.component.impl;

import com.pine.app.component.AbstractFormField;
import com.pine.inspection.FieldDTO;
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
    public void renderInternal() {
        ImGui.text(dto.getLabel());
        if (ImGui.dragInt(internalId, values, 1, dto.getMin(), dto.getMax())) {
            changerHandler.accept(dto, values[0]);
        }
    }
}
