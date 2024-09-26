package com.pine.component;

import com.pine.component.impl.*;
import com.pine.inspection.FieldDTO;
import com.pine.inspection.WithMutableData;
import com.pine.ui.panel.AbstractPanel;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.function.BiConsumer;

public class FormPanel extends AbstractPanel {
    private final BiConsumer<FieldDTO, Object> changeHandler;
    private final WithMutableData data;
    private String title;

    public FormPanel(WithMutableData data, BiConsumer<FieldDTO, Object> changeHandler) {
        this.data = data;
        this.changeHandler = changeHandler;
    }

    @Override
    public void onInitialize() {
        for (FieldDTO field : data.getFieldsAnnotated()) {
            switch (field.getType()) {
                case STRING:
                    appendChild(new StringField(field, changeHandler));
                    break;
                case CUSTOM:
                    if (ResourceRef.class.isAssignableFrom(field.getField().getType())) {
                        appendChild(new ResourceField(field, changeHandler));
                    }
                    break;
                case INT:
                    appendChild(new IntField(field, changeHandler));
                    break;
                case FLOAT:
                    appendChild(new FloatField(field, changeHandler));
                    break;
                case BOOLEAN:
                    appendChild(new BooleanField(field, changeHandler));
                    break;
                case VECTOR2:
                    appendChild(new Vector2Field(field, changeHandler));
                    break;
                case VECTOR3:
                    appendChild(new Vector3Field(field, changeHandler));
                    break;
                case VECTOR4:
                    appendChild(new Vector4Field(field, changeHandler));
                    break;
                case QUATERNION:
                    appendChild(new QuaternionField(field, changeHandler));
                    break;
                case COLOR:
                    appendChild(new ColorField(field, changeHandler));
                    break;
                case OPTIONS:
                    appendChild(new OptionsField(field, changeHandler));
                    break;
            }
        }
        this.title = data.getLabel() + internalId;
    }

    @Override
    public void renderInternal() {
        if (ImGui.collapsingHeader(title, ImGuiTreeNodeFlags.DefaultOpen)) {
            super.renderInternal();
        }
    }
}
