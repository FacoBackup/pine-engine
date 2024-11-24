package com.pine.editor.panels.component;

import com.pine.common.inspection.FieldDTO;
import com.pine.editor.core.AbstractView;

import java.util.function.BiConsumer;

public abstract class AbstractFormField extends AbstractView {
    protected final FieldDTO dto;
    protected final BiConsumer<FieldDTO, Object> changerHandler;

    public AbstractFormField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        this.dto = dto;
        this.changerHandler = changerHandler;
    }
}
