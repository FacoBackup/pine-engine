package com.pine.app.component.impl;

import com.pine.app.component.AbstractFormField;
import com.pine.inspection.FieldDTO;
import com.pine.ui.View;
import imgui.ImGui;

import java.util.function.BiConsumer;

public class ResourceField extends AbstractFormField {
    public ResourceField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
    }

    @Override
    public void renderInternal() {
//        if(ImGui.dragInt(dto.getLabel(), values, dto.getMin(), dto.getMax())){
//            changerHandler.accept(dto, values[0]);
//        }
    }
}
