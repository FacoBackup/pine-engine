package com.pine.editor.panels.viewport;

import com.pine.common.injection.PInject;
import com.pine.editor.core.AbstractView;
import com.pine.editor.core.UIUtil;
import com.pine.engine.repository.CameraRepository;
import com.pine.engine.service.world.WorldService;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiStyleVar;
import org.joml.Vector3f;

import static com.pine.editor.core.UIUtil.FIXED_WINDOW_FLAGS;
import static com.pine.editor.core.UIUtil.OPEN;

public class CameraPositionPanel extends AbstractView {
    private static final ImVec4 RED = new ImVec4(1, 0, 0, 1);
    private static final ImVec4 GREEN = new ImVec4(0, 1, 0, 1);
    private static final ImVec4 BLUE = new ImVec4(0, .5f, 1, 1);
    private static final float TO_DEG = (float) (180f / Math.PI);

    @PInject
    public CameraRepository cameraRepository;

    @PInject
    public WorldService worldService;

    @Override
    public void render() {
        ImGui.setNextWindowPos(ImGui.getWindowPosX(), ImGui.getWindowPosY() + ImGui.getWindowSizeY() - 25);
        ImGui.setNextWindowSize(ImGui.getWindowSizeX(), 16);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, UIUtil.DEFAULT_PADDING);
        ImGui.setNextWindowBgAlpha(.4f);
        if (ImGui.begin(imguiId + "cameraPos", OPEN, FIXED_WINDOW_FLAGS)) {
            Vector3f positionCamera = cameraRepository.currentCamera.position;
            ImGui.text("Current tile: " + worldService.getCurrentTile().getX() + " " + worldService.getCurrentTile().getZ());
            ImGui.sameLine();
            ImGui.textColored(RED, "X: " + (int) positionCamera.x);
            ImGui.sameLine();
            ImGui.textColored(GREEN, "Y: " + (int) positionCamera.y);
            ImGui.sameLine();
            ImGui.textColored(BLUE, "Z: " + (int) positionCamera.z);
            ImGui.sameLine();

            ImGui.text("Yaw: " + (int) (cameraRepository.currentCamera.yaw * TO_DEG));
            ImGui.sameLine();
            ImGui.text("Pitch: " + (int) (cameraRepository.currentCamera.pitch * TO_DEG));
        }
        ImGui.popStyleVar();
        ImGui.end();
    }
}
