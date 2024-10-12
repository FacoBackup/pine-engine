package com.pine.panels.component.impl;

import com.pine.inspection.FieldDTO;
import com.pine.panels.component.AbstractFormField;
import imgui.ImGui;

import java.util.function.BiConsumer;

public class BooleanField extends AbstractFormField {
    private boolean value;

    public BooleanField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        value = (Boolean) dto.getValue();
    }

    @Override
    public void render() {
        if(ImGui.checkbox(dto.getLabel(), value)){
            value = !value;
            changerHandler.accept(dto, value);
        }
    }
}
