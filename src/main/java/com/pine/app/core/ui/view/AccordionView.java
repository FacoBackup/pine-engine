package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.panel.AbstractPanel;
import imgui.ImGui;

public class AccordionView extends AbstractView {

    public AccordionView(View parent, String id, AbstractPanel panel) {
        super(parent, id, panel);
    }

    @Override
    public void render() {
        if (ImGui.collapsingHeader(innerText + internalId)) {
            super.render();
        }
    }
}
