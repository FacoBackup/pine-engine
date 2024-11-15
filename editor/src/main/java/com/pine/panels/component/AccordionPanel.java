package com.pine.panels.component;

import com.pine.core.AbstractView;
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
