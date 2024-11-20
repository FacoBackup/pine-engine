package com.pine.panels.viewport;

import com.pine.core.AbstractView;
import com.pine.core.UIUtil;
import com.pine.injection.PInject;
import com.pine.repository.CameraRepository;
import com.pine.repository.WorldRepository;
import com.pine.service.grid.WorldService;
import imgui.ImGui;
import imgui.ImVec4;
import org.joml.Vector3f;

import static com.pine.core.UIUtil.FIXED_WINDOW_FLAGS;
import static com.pine.core.UIUtil.OPEN;

public class CameraPositionPanel extends AbstractView {
    private static final ImVec4 RED = new ImVec4(1, 0, 0, 1);
    private static final ImVec4 GREEN = new ImVec4(0, 1, 0, 1);
    private static final ImVec4 BLUE = new ImVec4(0, .5f, 1, 1);

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public WorldService worldService;

    @Override
    public void render() {
        ImGui.setNextWindowPos(ImGui.getWindowPosX() + 8, ImGui.getWindowPosY() + ImGui.getWindowSizeY() - 25);
        ImGui.setNextWindowSize(ImGui.getWindowSizeX() - 16, 16);
        if (ImGui.begin(imguiId + "cameraPos", OPEN, FIXED_WINDOW_FLAGS)) {
            Vector3f positionCamera = cameraRepository.currentCamera.position;
            ImGui.text("Current tile: " + worldService.getCurrentTile().getX() + " " + worldService.getCurrentTile().getZ());
            ImGui.sameLine();
            ImGui.textColored(RED, "X: " + (int) positionCamera.x);
            ImGui.sameLine();
            ImGui.textColored(GREEN, "Y: " + (int) positionCamera.y);
            ImGui.sameLine();
            ImGui.textColored(BLUE, "Z: " + (int) positionCamera.z);
        }
        ImGui.end();
    }
}
