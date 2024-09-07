package com.pine.app.editor.panels.viewport;

import com.pine.app.core.ui.panel.AbstractWindowPanel;
import com.pine.app.editor.EditorWindow;
import com.pine.engine.Engine;
import imgui.ImGui;

public class ViewportPanel extends AbstractWindowPanel {
    private Engine engine;

    @Override
    public void onInitialize() {
        super.onInitialize();
        engine = ((EditorWindow) document.getWindow()).getEngine();
    }

    @Override
    protected String getTitle() {
        return "Viewport";
    }

    @Override
    protected void afterWindow() {
        engine.setInputFocused(ImGui.isWindowFocused());
    }
}
