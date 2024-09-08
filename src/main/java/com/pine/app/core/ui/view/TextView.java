package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import imgui.ImGui;

public class TextView extends AbstractView {
    public TextView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void renderInternal() {
        if (innerText != null) {
            ImGui.text(getInnerText());
        }
    }
}
