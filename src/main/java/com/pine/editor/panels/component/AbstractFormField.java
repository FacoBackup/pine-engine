package com.pine.editor.panels.component;

import com.pine.common.inspection.FieldDTO;

import java.util.function.BiConsumer;

public abstract class AbstractFormField extends AbstractFieldView {
    protected final FieldDTO dto;
    protected final BiConsumer<FieldDTO, Object> changeHandler;

    public AbstractFormField(FieldDTO dto, BiConsumer<FieldDTO, Object> changeHandler) {
        super(dto.getLabel());
        this.dto = dto;
        this.changeHandler = changeHandler;
    }
}
