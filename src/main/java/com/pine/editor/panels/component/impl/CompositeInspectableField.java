package com.pine.editor.panels.component.impl;

import com.pine.common.inspection.FieldDTO;
import com.pine.common.inspection.Inspectable;
import com.pine.editor.panels.component.AbstractFormField;
import com.pine.editor.panels.component.FormPanel;

import java.util.function.BiConsumer;

public class CompositeInspectableField extends AbstractFormField {
    private final Inspectable value;

    public CompositeInspectableField(FieldDTO field, BiConsumer<FieldDTO, Object> changeHandler) {
        super(field, changeHandler);
        value = (Inspectable) field.getValue();
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        FormPanel form = appendChild(new FormPanel(changeHandler));
        form.setInspection(value);
    }
}
