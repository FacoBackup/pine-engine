package com.pine.ui.view;

import com.pine.ui.View;
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
