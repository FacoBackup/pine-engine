package com.pine.panels.component.impl;

import com.pine.inspection.FieldDTO;
import com.pine.panels.component.AbstractFormField;
import imgui.ImGui;
import imgui.type.ImString;

import java.util.function.BiConsumer;

public class StringField extends AbstractFormField {
    private final ImString value = new ImString();

    public StringField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        value.set(dto.getValue());
    }

    @Override
    public void render() {
        ImGui.text(dto.getLabel());
        if(ImGui.inputText(dto.getId(), value)){
            changerHandler.accept(dto, value.get());
        }
    }
}
