package com.pine.editor.panels.component.impl;

import com.pine.common.inspection.FieldDTO;
import com.pine.editor.panels.component.AbstractFormField;
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
        if (dto.isDisabled()) {
            ImGui.textDisabled(value.get());
        } else {
            if (ImGui.inputText(dto.getId(), value)) {
                changerHandler.accept(dto, value.get());
            }
        }
    }
}
