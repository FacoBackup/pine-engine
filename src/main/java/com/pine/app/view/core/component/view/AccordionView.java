package com.pine.app.view.core.component.view;

import com.pine.app.view.core.component.View;
import com.pine.app.view.core.component.panel.AbstractPanel;
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
