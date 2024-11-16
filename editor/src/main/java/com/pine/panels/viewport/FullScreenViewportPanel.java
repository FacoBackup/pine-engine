package com.pine.panels.viewport;

import com.pine.Engine;
import com.pine.core.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.EditorRepository;
import com.pine.repository.RuntimeRepository;
import com.pine.service.camera.CameraMovementService;
import com.pine.service.camera.Camera;
import com.pine.service.resource.fbo.FrameBufferObject;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseButton;
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
