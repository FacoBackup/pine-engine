package com.pine.editor.panels.component;

import com.pine.common.inspection.FieldDTO;
import com.pine.editor.core.AbstractView;

import java.util.function.BiConsumer;

public abstract class AbstractFieldView extends AbstractView {
    private final String name;

    public AbstractFieldView(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
