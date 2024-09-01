package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import imgui.ImGui;

public class ButtonView extends AbstractView {
    private Runnable onClick;

    public ButtonView(View parent, String id) {
        super(parent, id);
    }

    @Override
    protected void renderInternal() {
        if (!visible) {
            return;
        }

        if (ImGui.button(innerText + internalId) && onClick != null) {
            onClick.run();
        }
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }
}
