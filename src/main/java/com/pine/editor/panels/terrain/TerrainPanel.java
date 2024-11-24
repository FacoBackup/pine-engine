package com.pine.editor.panels.terrain;

import com.pine.editor.core.dock.AbstractDockPanel;
import com.pine.editor.panels.inspector.FoliagePanel;
import com.pine.editor.panels.inspector.MaterialPanel;

public class TerrainPanel extends AbstractDockPanel {
    @Override
    public void onInitialize() {
        appendChild(new FoliagePanel());
        appendChild(new MaterialPanel());
    }
}
