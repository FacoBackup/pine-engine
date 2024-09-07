package com.pine.app.core.ui.view;

import com.pine.app.core.Icon;
import com.pine.app.core.ui.View;
import imgui.ImGui;

public class ButtonView extends AbstractView {
    private Runnable onClick;
    private String labelWithId;

    public ButtonView(View parent, String id) {
        super(parent, id);
    }

    @Override
    protected void renderInternal() {
        if (labelWithId != null && onClick != null && ImGui.button(labelWithId)) {
            onClick.run();
        }
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        setInnerText(innerText);
    }

    @Override
    public void setInnerText(String textContent) {
        super.setInnerText(textContent);
        processIcons();
        labelWithId = innerText + internalId;
    }

    private void processIcons() {
        if (innerText != null) {
            for (var icon : Icon.values()) {
                innerText = innerText.replace("[" + icon.getIconName() + "]", icon.getCodePoint());
            }
        }
    }
}
