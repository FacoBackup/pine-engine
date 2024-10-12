package com.pine.panels.component.impl;

import com.pine.component.Transformation;
import com.pine.inspection.FieldDTO;
import com.pine.panels.component.AbstractFormField;
import com.pine.panels.component.FormPanel;

import java.util.function.BiConsumer;

public class TransformationField extends AbstractFormField {
    private final Transformation transformation;
    private FormPanel formPanel;

    public TransformationField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        super(dto, changerHandler);
        transformation = (Transformation) dto.getValue();
        transformation.getFieldsAnnotated();
    }

    @Override
    public void onInitialize() {
        formPanel = appendChild(new FormPanel(changerHandler));
    }

    @Override
    public void render() {
        formPanel.setInspectable(transformation);
        formPanel.render();
    }
}
