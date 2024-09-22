package com.pine.app.panels.viewport;

import com.pine.Engine;
import com.pine.PInject;
import com.pine.repository.RuntimeRepository;
import com.pine.service.resource.fbo.FBO;
import com.pine.service.resource.fbo.FBOCreationData;
import com.pine.ui.panel.AbstractWindowPanel;
import imgui.ImGui;
import imgui.flag.ImGuiKey;

public class ViewportPanel extends AbstractWindowPanel {
    @PInject
    public Engine engine;

    private RuntimeRepository repo;
    private FBO fbo;

    @Override
    public void onInitialize() {
        super.onInitialize();
        repo = engine.getRuntimeRepository();
        padding.x = 0;
        padding.y = 0;
        this.fbo = (FBO) engine.getResourceService().addResource(new FBOCreationData(false, false).addSampler());
    }

    @Override
    protected String getTitle() {
        return "Viewport";
    }

    @Override
    public void tick() {
        engine.setTargetFBO(fbo);
        engine.render();
    }

    @Override
    public void renderInternal() {
        ImGui.image(engine.getTargetFBO().getMainSampler(), size.x , size.y - FRAME_SIZE);
    }

    @Override
    protected void afterWindow() {
        repo.inputFocused = ImGui.isWindowFocused() && (ImGui.isMouseDown(2) || ImGui.isMouseDown(1));
        repo.fasterPressed = ImGui.isKeyPressed(ImGuiKey.LeftShift);
        repo.forwardPressed = ImGui.isKeyPressed(ImGuiKey.W);
        repo.backwardPressed = ImGui.isKeyPressed(ImGuiKey.S);
        repo.leftPressed = ImGui.isKeyPressed(ImGuiKey.A);
        repo.rightPressed = ImGui.isKeyPressed(ImGuiKey.D);
        repo.upPressed = ImGui.isKeyPressed(ImGuiKey.Space);
        repo.downPressed = ImGui.isKeyPressed(ImGuiKey.LeftCtrl);
        repo.mouseX = ImGui.getMousePosX();
        repo.mouseY = ImGui.getMousePosY();
        repo.viewportH = size.y;
        repo.viewportW = size.x;
    }
}
