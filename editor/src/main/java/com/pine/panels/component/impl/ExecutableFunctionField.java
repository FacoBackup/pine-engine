package com.pine.panels.component.impl;

import com.pine.core.view.AbstractView;
import com.pine.inspection.FieldDTO;
import com.pine.inspection.MethodDTO;
import com.pine.panels.component.AbstractFormField;
import imgui.ImGui;

import java.util.function.BiConsumer;

public class ExecutableFunctionField extends AbstractView {
    private final MethodDTO dto;

    public ExecutableFunctionField(MethodDTO dto) {
        this.dto = dto;
    }

    @Override
    public void render() {
        if (ImGui.button(dto.getLabel())) {
            try {
                dto.getMethod().invoke(dto.getInstance());
            } catch (Exception e) {
                getLogger().error("Could not execute method", e);
            }
        }
    }
}
