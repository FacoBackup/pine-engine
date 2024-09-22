package com.pine.component.impl;

import com.pine.component.AbstractFormField;
import com.pine.inspection.FieldDTO;
import imgui.ImGui;

import java.util.function.BiConsumer;

public class BooleanField extends AbstractFormField {
    private boolean value;

    public BooleanField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        value = (Boolean) dto.getValue();
    }

    @Override
    public void renderInternal() {
        if(ImGui.checkbox(dto.getLabel(), value)){
            value = !value;
            changerHandler.accept(dto, value);
        }
    }
}
