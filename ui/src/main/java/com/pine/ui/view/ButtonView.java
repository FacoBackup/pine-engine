package com.pine.ui.view;

import com.pine.ui.View;
import imgui.ImGui;

public class ButtonView extends AbstractView {
    private Runnable onClick;
    private String labelWithId;

    public ButtonView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void renderInternal() {
        if (labelWithId != null && onClick != null && ImGui.button(labelWithId)) {
            onClick.run();
        }
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    @Override
    public void setInnerText(String textContent) {
        super.setInnerText(textContent);
        labelWithId = innerText + internalId;
    }
}
