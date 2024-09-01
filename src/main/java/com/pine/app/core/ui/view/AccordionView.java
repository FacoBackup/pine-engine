package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.panel.AbstractPanel;
import imgui.ImGui;

public class AccordionView extends AbstractView {
    public AccordionView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void render() {
        if (ImGui.collapsingHeader(innerText + internalId)) {
            super.render();
        }
    }
}
