package com.pine.component;

import com.pine.component.impl.*;
import com.pine.inspection.FieldDTO;
import com.pine.inspection.Inspectable;
import com.pine.view.AbstractView;
import imgui.ImGui;

import java.util.function.BiConsumer;

public class FormPanel extends AbstractView {
    private final BiConsumer<FieldDTO, Object> changeHandler;
    private Inspectable inspectable;
    private String title;

    public FormPanel(BiConsumer<FieldDTO, Object> changeHandler) {
        this.changeHandler = changeHandler;
    }

    public void setInspectable(Inspectable data) {
        if (this.inspectable == data) {
            return;
        }
        this.inspectable = data;
        children.clear();
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
        this.title = data.getTitle() + imguiId;
    }

    public Inspectable getInspectable() {
        return inspectable;
    }

    @Override
    public void renderInternal() {
        ImGui.text(title);
        super.renderInternal();
    }
}
