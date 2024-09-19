package com.pine.app.panels.inspector;

import com.pine.app.component.FormPanel;
import com.pine.component.TransformationComponent;
import com.pine.ui.View;
import com.pine.ui.panel.AbstractWindowPanel;

public class InspectorPanel extends AbstractWindowPanel {
    TransformationComponent comp = new TransformationComponent();
    @Override
    protected String getTitle() {
        return "Inspector";
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        appendChild(new FormPanel(comp, (d, dd) -> {

        }));
    }
}
