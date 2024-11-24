package com.pine.editor.panels.component;

import com.pine.common.inspection.FieldDTO;
import com.pine.common.inspection.Inspectable;
import com.pine.common.inspection.MethodDTO;
import com.pine.editor.core.AbstractView;
import com.pine.editor.panels.component.impl.*;
import com.pine.engine.inspection.ResourceTypeField;
import com.pine.engine.inspection.TypePreviewField;
import imgui.ImGui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class FormPanel extends AbstractView {
    private final BiConsumer<FieldDTO, Object> changeHandler;
    private Inspectable inspectable;

    public FormPanel(BiConsumer<FieldDTO, Object> changeHandler) {
        this.changeHandler = changeHandler;
    }

    public void setInspection(Inspectable data) {
        if (this.inspectable == data) {
            return;
        }
        this.inspectable = data;
        children.clear();

        if (data == null) {
            return;
        }
        Map<String, AccordionPanel> groups = new HashMap<>();
        processMethods(data, groups);
        processFields(data, groups);
    }

    private void processMethods(Inspectable data, Map<String, AccordionPanel> groups) {
        for (MethodDTO methodDTO : data.getMethodsAnnotated()) {
            if (!groups.containsKey(methodDTO.getGroup())) {
                groups.put(methodDTO.getGroup(), appendChild(new AccordionPanel()));
            }

            AccordionPanel group = groups.get(methodDTO.getGroup());
            group.title = methodDTO.getGroup();
            group.appendChild(new ExecutableFunctionField(methodDTO));
        }
    }

    private void processFields(Inspectable data, Map<String, AccordionPanel> groups) {
        for (FieldDTO field : data.getFieldsAnnotated()) {
            if (!groups.containsKey(field.getGroup())) {
                groups.put(field.getGroup(), appendChild(new AccordionPanel()));
            }

            AccordionPanel group = groups.get(field.getGroup());
            group.title = field.getGroup();
            switch (field.getType()) {
                case STRING:
                    if (field.getField().isAnnotationPresent(ResourceTypeField.class)) {
                        group.appendChild(new ResourceField(field, changeHandler));
                    } else if (field.getField().isAnnotationPresent(TypePreviewField.class)) {
                        group.appendChild(new PreviewField(field, changeHandler));
                    } else {
                        group.appendChild(new StringField(field, changeHandler));
                    }
                    break;
                case INT:
                    group.appendChild(new IntField(field, changeHandler));
                    break;
                case FLOAT:
                    group.appendChild(new FloatField(field, changeHandler));
                    break;
                case BOOLEAN:
                    group.appendChild(new BooleanField(field, changeHandler));
                    break;
                case VECTOR2:
                    group.appendChild(new Vector2Field(field, changeHandler));
                    break;
                case VECTOR3:
                    group.appendChild(new Vector3Field(field, changeHandler));
                    break;
                case VECTOR4:
                    group.appendChild(new Vector4Field(field, changeHandler));
                    break;
                case QUATERNION:
                    group.appendChild(new QuaternionField(field, changeHandler));
                    break;
                case COLOR:
                    group.appendChild(new ColorField(field, changeHandler));
                    break;
                case OPTIONS:
                    group.appendChild(new OptionsField(field, changeHandler));
                    break;
            }
        }
    }

    public Inspectable getInspectable() {
        return inspectable;
    }

    @Override
    public void render() {
        if (inspectable != null) {
            ImGui.text(inspectable.getIcon() + inspectable.getTitle());
            ImGui.separator();
            super.render();
        }
    }
}
