package com.pine.editor.panels.component.impl;

import com.pine.common.inspection.FieldDTO;
import com.pine.editor.panels.component.AbstractFormField;
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
        if (dto.isDisabled()) {
            ImGui.textDisabled(dto.getLabel() + ": " + value);
        } else {
            if (ImGui.checkbox(dto.getLabel(), value)) {
                value = !value;
                changerHandler.accept(dto, value);
            }
        }
    }
}
