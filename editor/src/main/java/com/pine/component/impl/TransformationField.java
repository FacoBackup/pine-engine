package com.pine.component.impl;

import com.pine.component.AbstractFormField;
import com.pine.component.FormPanel;
import com.pine.component.Transformation;
import com.pine.inspection.FieldDTO;

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
    public void renderInternal() {
        formPanel.setInspectable(transformation);
        formPanel.renderInternal();
    }
}
