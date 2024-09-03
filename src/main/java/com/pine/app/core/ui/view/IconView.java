package com.pine.app.core.ui.view;

import com.pine.app.core.ui.MaterialIcon;
import com.pine.app.core.ui.View;
import imgui.ImGui;

public class IconView extends AbstractView {
    private MaterialIcon icon;

    public IconView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        icon = MaterialIcon.iconOfName(innerText);
    }

    @Override
    protected void renderInternal() {
        if (icon == null) {
            return;
        }
        ImGui.text(icon.getCodePoint());
    }

    public void setIcon(MaterialIcon icon) {
        this.icon = icon;
    }
}
