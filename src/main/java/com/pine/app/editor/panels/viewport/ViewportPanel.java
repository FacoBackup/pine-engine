package com.pine.app.editor.panels.viewport;

import com.pine.app.core.ui.panel.AbstractWindowPanel;
import com.pine.app.editor.EditorWindow;
import com.pine.engine.core.RuntimeRepository;
import imgui.ImGui;
import imgui.flag.ImGuiKey;

public class ViewportPanel extends AbstractWindowPanel {
    private RuntimeRepository repo;

    @Override
    public void onInitialize() {
        super.onInitialize();
        repo = ((EditorWindow) document.getWindow()).getEngine().getRuntimeRepository();
    }

    @Override
    protected String getTitle() {
        return "Viewport";
    }

    @Override
    protected void afterWindow() {
        repo.setInputFocused(ImGui.isWindowFocused() && ImGui.isMouseClicked(0));
        repo.setForwardPressed(ImGui.isKeyPressed(ImGuiKey.W));
        repo.setBackwardPressed(ImGui.isKeyPressed(ImGuiKey.S));
        repo.setLeftPressed(ImGui.isKeyPressed(ImGuiKey.A));
        repo.setRightPressed(ImGui.isKeyPressed(ImGuiKey.D));
        repo.setUpPressed(ImGui.isKeyPressed(ImGuiKey.Space));
        repo.setDownPressed(ImGui.isKeyPressed(ImGuiKey.LeftShift));
        repo.setMouseX(ImGui.getMousePosX());
        repo.setMouseY(ImGui.getMousePosY());
        repo.setViewportH(size.y);
        repo.setViewportW(size.x);
        repo.windowW = document.getWindow().getDisplayW();
        repo.windowH = document.getWindow().getDisplayH();
    }
}
