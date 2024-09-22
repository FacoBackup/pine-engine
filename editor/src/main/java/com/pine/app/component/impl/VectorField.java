package com.pine.app.component.impl;

import com.pine.app.component.AbstractFormField;
import com.pine.inspection.FieldDTO;
import imgui.ImGui;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.BiConsumer;

public class VectorField extends AbstractFormField {
    private final float[] values = new float[4];

    public VectorField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        switch (dto.getType()) {
            case VECTOR2: {
                var cast = (Vector2f) dto.getValue();
                values[0] = cast.x;
                values[1] = cast.y;
                break;
            }
            case VECTOR3: {
                var cast = (Vector3f) dto.getValue();
                values[0] = cast.x;
                values[1] = cast.y;
                values[2] = cast.z;
                break;
            }
            case QUATERNION:{
                var cast = (Quaternionf) dto.getValue();
                values[0] = cast.x;
                values[1] = cast.y;
                values[2] = cast.z;
                values[3] = cast.w;
                break;
            }
            case VECTOR4: {
                var cast = (Vector4f) dto.getValue();
                values[0] = cast.x;
                values[1] = cast.y;
                values[2] = cast.z;
                values[3] = cast.w;
                break;
            }
        }

    }

    @Override
    public void renderInternal() {
        ImGui.text(dto.getLabel());
        switch (dto.getType()) {
            case VECTOR2 -> ImGui.dragFloat2(internalId, values, .01f, dto.getMin(), dto.getMax());
            case VECTOR3 -> ImGui.dragFloat3(internalId, values, .01f, dto.getMin(), dto.getMax());
            case VECTOR4, QUATERNION -> ImGui.dragFloat4(internalId, values, .01f, dto.getMin(), dto.getMax());
        }
    }
}
