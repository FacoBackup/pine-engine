package com.pine.panels.viewport;

import com.pine.core.AbstractView;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector3f;

import static com.pine.core.UIUtil.FIXED_WINDOW_FLAGS;
import static com.pine.core.UIUtil.OPEN;

public class CameraPositionPanel extends AbstractView {
    private static final ImVec4 RED = new ImVec4(1, 0, 0, 1);
    private static final ImVec4 GREEN = new ImVec4(0, 1, 0, 1);
    private static final ImVec4 BLUE = new ImVec4(0, .5f, 1, 1);

    @PInject
    public CameraRepository cameraRepository;

    @Override
    public void render() {
        ImGui.setNextWindowPos(ImGui.getWindowPosX() + 8, ImGui.getWindowPosY() + ImGui.getWindowSizeY() - 25);
        ImGui.setNextWindowSize(ImGui.getWindowSizeX() - 16, 16);
        if (ImGui.begin(imguiId + "cameraPos", OPEN, FIXED_WINDOW_FLAGS)) {
            Vector3f positionCamera = cameraRepository.currentCamera.position;
            ImGui.textColored(RED, "X: " + positionCamera.x);
            ImGui.sameLine();
            ImGui.textColored(GREEN, "Y: " + positionCamera.y);
            ImGui.sameLine();
            ImGui.textColored(BLUE, "Z: " + positionCamera.z);
        }
        ImGui.end();
    }
}
