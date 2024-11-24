package com.pine.editor.panels.component.impl;

import com.pine.common.inspection.FieldDTO;
import com.pine.common.inspection.SelectableEnum;
import com.pine.editor.panels.component.AbstractFormField;
import imgui.ImGui;

import java.util.function.BiConsumer;

public class OptionsField extends AbstractFormField {
    private SelectableEnum selected;

    public OptionsField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        selected = (SelectableEnum) dto.getValue();
    }

    @Override
    public void render() {
        if (dto.isDisabled()) {
            ImGui.text(dto.getLabel() + ": ");
            ImGui.textDisabled((selected == null ? "None" : selected.getTitle()));
        } else {
            ImGui.text(dto.getLabel());
            for (var op : dto.getOptions()) {
                if (ImGui.checkbox(op.getTitle(), selected == op)) {
                    selected = op;
                    changerHandler.accept(dto, op);
                }
            }
        }
    }
}
