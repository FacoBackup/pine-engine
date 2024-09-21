package com.pine.app.component.impl;

import com.pine.app.component.AbstractFormField;
import com.pine.component.SelectableEnum;
import com.pine.inspection.FieldDTO;
import imgui.ImGui;

import java.util.function.BiConsumer;

public class OptionsField extends AbstractFormField {
    private SelectableEnum selected;
    public OptionsField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
    }

    @Override
    public void renderInternal() {
        ImGui.text(dto.getLabel());
        for(var op : dto.getOptions()){
            if(ImGui.checkbox(op.getLabel(), selected == op)){
                selected = op;
                changerHandler.accept(dto, op);
            }
        }
    }
}
