package com.pine.panels.component.impl;

import com.pine.inspection.Color;
import com.pine.inspection.FieldDTO;
import com.pine.panels.component.AbstractFormField;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiColorEditFlags;

import java.util.function.BiConsumer;

public class ColorField extends AbstractFormField {
    private final ImVec4 values = new ImVec4();
    private final float[] valuesV = new float[3];

    public ColorField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        var cast = (Color) dto.getValue();
        valuesV[0] = values.x = cast.x;
        valuesV[1] = values.y = cast.y;
        valuesV[2] = values.z = cast.z;
        values.w = 1;
    }

    @Override
    public void render() {
        if(dto.isDisabled()){
            ImGui.colorButton(dto.getId(), values);
        }else {
            ImGui.text(dto.getLabel());
            if (ImGui.colorPicker3(dto.getId(), valuesV, ImGuiColorEditFlags.NoSidePreview | ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoAlpha)) {
                changerHandler.accept(dto, valuesV);
            }
        }
    }
}
