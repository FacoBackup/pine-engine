package com.pine.panels.component;

import com.pine.core.view.AbstractView;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

public class AccordionPanel extends AbstractView {
    public String title;

    @Override
    public void render() {
        if (title.isEmpty()) {
            super.render();
            return;
        }

        if (ImGui.collapsingHeader(title + imguiId, ImGuiTreeNodeFlags.DefaultOpen)) {
            super.render();
        }
    }
}
