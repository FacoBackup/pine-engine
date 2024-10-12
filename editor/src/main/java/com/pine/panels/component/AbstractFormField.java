package com.pine.panels.component;

import com.pine.core.view.AbstractView;
import com.pine.inspection.FieldDTO;

import java.util.function.BiConsumer;

public abstract class AbstractFormField extends AbstractView {
    protected final FieldDTO dto;
    protected final BiConsumer<FieldDTO, Object> changerHandler;

    public AbstractFormField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        this.dto = dto;
        this.changerHandler = changerHandler;
    }
}
