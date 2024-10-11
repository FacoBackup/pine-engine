package com.pine.panels.resources;

import com.pine.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.repository.rendering.RenderingRepository;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.repository.voxelization.VoxelizerRepository;
import com.pine.service.streaming.mesh.MeshService;
import com.pine.service.streaming.texture.TextureService;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;

import static com.pine.panels.console.ConsolePanel.TABLE_FLAGS;


public class ResourcesPanel extends AbstractDockPanel {
    @PInject
    public RenderingRepository renderingRepository;

    @PInject
    public MeshService meshService;

    @PInject
    public TextureService textureService;

    @PInject
    public VoxelizerRepository voxelRepository;

    @PInject
    public StreamingRepository streamingRepository;

    @Override
    public void renderInternal() {
        if (ImGui.beginTable("##resources" + imguiId, 2, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("Data", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Quantity", ImGuiTableColumnFlags.WidthFixed, 120f);
            ImGui.tableHeadersRow();

            ImGui.tableNextRow();
            ImGui.tableNextColumn();
            ImGui.text("Total triangles");
            ImGui.tableNextColumn();
            ImGui.text(String.valueOf(meshService.getTotalTriangleCount()));

            ImGui.tableNextRow();
            ImGui.tableNextColumn();
            ImGui.text("Triangles being rendered");
            ImGui.tableNextColumn();
            ImGui.text(String.valueOf(renderingRepository.getTotalTriangleCount()));

            ImGui.tableNextRow();
            ImGui.tableNextColumn();
            ImGui.text("Textures");
            ImGui.tableNextColumn();
            ImGui.text(String.valueOf(textureService.getTotalTextureCount()));

            ImGui.tableNextRow();
            ImGui.tableNextColumn();
            ImGui.text("Voxels");
            ImGui.tableNextColumn();
            ImGui.text(String.valueOf(voxelRepository.getVoxelCount()));

            ImGui.tableNextRow();
            ImGui.tableNextColumn();
            ImGui.text("Resources to be streamed in");
            ImGui.tableNextColumn();
            ImGui.text(String.valueOf(streamingRepository.schedule.size()));

            ImGui.endTable();
        }
    }
}

