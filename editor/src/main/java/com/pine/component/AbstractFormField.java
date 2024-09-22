package com.pine.component;

import com.pine.inspection.FieldDTO;
import com.pine.ui.panel.AbstractPanel;

import java.util.function.BiConsumer;

public abstract class AbstractFormField extends AbstractPanel {
    protected final FieldDTO dto;
    protected final BiConsumer<FieldDTO, Object> changerHandler;

    public AbstractFormField(FieldDTO dto, BiConsumer<FieldDTO, Object> changerHandler) {
        this.dto = dto;
        this.changerHandler = changerHandler;
    }
}
