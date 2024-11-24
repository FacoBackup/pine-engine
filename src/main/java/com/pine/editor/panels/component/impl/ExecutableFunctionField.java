package com.pine.editor.panels.component.impl;

import com.pine.common.inspection.MethodDTO;
import com.pine.editor.core.AbstractView;
import imgui.ImGui;

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
