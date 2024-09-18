package com.pine.ui.view;

import com.pine.ui.View;
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
