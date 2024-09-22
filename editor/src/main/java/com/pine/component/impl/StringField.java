package com.pine.component.impl;

import com.pine.component.AbstractFormField;
import com.pine.inspection.FieldDTO;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

import java.util.function.BiConsumer;

public class StringField extends AbstractFormField {
    private final ImString value = new ImString();

    public StringField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        value.set(dto.getValue());
    }

    @Override
    public void renderInternal() {
        ImGui.text(dto.getLabel());
        if(ImGui.inputText(dto.getLabel(), value, ImGuiInputTextFlags.EnterReturnsTrue)){
            changerHandler.accept(dto, value.get());
        }
    }
}
