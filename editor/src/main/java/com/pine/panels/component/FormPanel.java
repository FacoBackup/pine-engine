package com.pine.panels.component;

import com.pine.component.Transformation;
import com.pine.inspection.FieldDTO;
import com.pine.inspection.Inspectable;
import com.pine.panels.component.impl.*;
import com.pine.repository.streaming.AbstractStreamableResource;
import com.pine.view.AbstractView;
import imgui.ImGui;

import java.util.function.BiConsumer;

public class FormPanel extends AbstractView {
    private final BiConsumer<FieldDTO, Object> changeHandler;
    private Inspectable inspectable;

    public FormPanel(BiConsumer<FieldDTO, Object> changeHandler) {
        this.changeHandler = changeHandler;
    }

    public void setInspectable(Inspectable data) {
        if (this.inspectable == data) {
            return;
        }
        this.inspectable = data;
        children.clear();

        if (data == null) {
            return;
        }

        for (FieldDTO field : data.getFieldsAnnotated()) {
            switch (field.getType()) {
                case CUSTOM:
                    if (AbstractStreamableResource.class.isAssignableFrom(field.getField().getType())) {
                        appendChild(new ResourceField(field, changeHandler));
                    }
                    if(Transformation.class.isAssignableFrom(field.getField().getType())){
                        appendChild(new TransformationField(field, changeHandler));
                    }
                    break;
                case STRING:
                    appendChild(new StringField(field, changeHandler));
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
    }

    public Inspectable getInspectable() {
        return inspectable;
    }

    @Override
    public void renderInternal() {
        if (inspectable != null) {
            ImGui.text(inspectable.getTitle());
            super.renderInternal();
        }
    }
}
