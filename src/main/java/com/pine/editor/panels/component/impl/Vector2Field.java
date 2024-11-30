package com.pine.editor.panels.component.impl;

import com.pine.common.inspection.FieldDTO;
import com.pine.editor.panels.component.AbstractFormField;
import imgui.ImGui;
import org.joml.Vector2f;

import java.util.function.BiConsumer;

public class Vector2Field extends AbstractFormField {
    private final float[] values = new float[4];
    private final Vector2f valVec2;

    public Vector2Field(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        valVec2 = (Vector2f) dto.getValue();
    }

    @Override
    public void render() {
        values[0] = valVec2.x;
        values[1] = valVec2.y;
        ImGui.text(dto.getLabel());

        if (dto.isDisabled()) {
            ImGui.text("X: " + values[0]);
            ImGui.text("Y: " + values[1]);
        } else {
            if (ImGui.dragFloat2(imguiId, values, .01f, dto.getMin(), dto.getMax())) {
                changeHandler.accept(dto, values);
            }
        }
    }
}
