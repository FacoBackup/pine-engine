package com.pine.editor.panels.component.impl;

import com.pine.common.inspection.MethodDTO;
import com.pine.editor.core.AbstractView;
import com.pine.editor.panels.component.AbstractFieldView;
import imgui.ImGui;

public class ExecutableFunctionField extends AbstractFieldView {
    private final MethodDTO dto;

    public ExecutableFunctionField(MethodDTO dto) {
        super(dto.getLabel());
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
