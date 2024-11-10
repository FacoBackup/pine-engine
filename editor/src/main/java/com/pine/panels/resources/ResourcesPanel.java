package com.pine.panels.resources;

import com.pine.core.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.streaming.impl.TextureService;
import com.pine.service.voxelization.VoxelizationService;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;

import static com.pine.panels.console.ConsolePanel.TABLE_FLAGS;


public class ResourcesPanel extends AbstractDockPanel {
    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public TextureService textureService;

    @PInject
    public VoxelizationService voxelizationService;

    @PInject
    public StreamingRepository streamingRepository;

    @Override
    public void render() {
        if (ImGui.beginTable("##resources" + imguiId, 2, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("Data", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Quantity", ImGuiTableColumnFlags.WidthFixed, 120f);
            ImGui.tableHeadersRow();

            render("Individual draw calls", renderingRepository.getDrawCallQuantity());

            render("Triangles being rendered", renderingRepository.getTotalTriangleCount());

            render("Textures", textureService.getTotalTextureCount());

            render("Voxels", voxelizationService.getVoxelCount());

            render("Resources to be streamed in", streamingRepository.toStreamIn.size());

            render("Resources to be streamed in", streamingRepository.toStreamIn.size());


            ImGui.endTable();
        }
    }

    private void render(String Resources_to_be_streamed_in, int schedule) {
        ImGui.tableNextRow();
        ImGui.tableNextColumn();
        ImGui.text(Resources_to_be_streamed_in);
        ImGui.tableNextColumn();
        ImGui.text(String.valueOf(schedule));
    }
}

