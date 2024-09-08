package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import imgui.ImGui;

public class AccordionView extends AbstractView {
    public AccordionView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void renderInternal() {
        if (ImGui.collapsingHeader(innerText + internalId)) {
            super.renderInternal();
        }
    }
}
