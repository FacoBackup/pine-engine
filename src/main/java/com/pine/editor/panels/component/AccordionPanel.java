package com.pine.editor.panels.component;

import com.pine.editor.core.AbstractView;
import imgui.ImGui;

public class AccordionPanel extends AbstractView {
    public String title;

    @Override
    public void render() {
        if (title.isEmpty()) {
            super.render();
            return;
        }

        if (ImGui.collapsingHeader(title + imguiId)) {
            super.render();
        }
    }
}
