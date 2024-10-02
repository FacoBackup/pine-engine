package com.pine.component.impl;

import com.pine.component.AbstractFormField;
import com.pine.inspection.Color;
import com.pine.inspection.FieldDTO;
import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;

import java.util.function.BiConsumer;

import static java.lang.Float.NaN;

public class ColorField extends AbstractFormField {
    private final float[] values = new float[4];

    public ColorField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        var cast = (Color) dto.getValue();
        values[0] = cast.x;
        values[1] = cast.y;
        values[2] = cast.z;
    }

    @Override
    public void renderInternal() {
        ImGui.text(dto.getLabel());
        if (ImGui.colorPicker3(dto.getId(), values, ImGuiColorEditFlags.NoSidePreview | ImGuiColorEditFlags.DisplayRGB | ImGuiColorEditFlags.NoAlpha)) {
            changerHandler.accept(dto, values);
        }
    }
}
