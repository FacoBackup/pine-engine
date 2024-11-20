package com.pine.panels.viewport;

import com.pine.injection.PInject;
import com.pine.repository.EditorRepository;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;

import static com.pine.core.AbstractWindow.FLAGS;
import static com.pine.core.UIUtil.OPEN;

public class FullScreenViewportPanel extends AbstractViewportPanel {
    private static final ImVec2 PADDING_NONE = new ImVec2(0, 0);

    @PInject
    public EditorRepository editorRepository;

    @Override
    public void render() {
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(ImGui.getMainViewport().getSize());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, PADDING_NONE);
        ImGui.begin("FullScreenViewportPanel", OPEN, FLAGS);
        ImGui.popStyleVar();
        updateCamera();
        tick();
        renderFrame();
        ImGui.end();

        if (ImGui.begin("fps")) {
            ImGui.text(io.getFramerate() + "fps");
        }
        ImGui.end();
    }

    @Override
    protected String getCameraId() {
        return imguiId;
    }
}
