package com.pine.panels.resources;

import com.pine.component.MeshComponent;
import com.pine.core.dock.AbstractDockPanel;
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

    @PInject
    public MeshComponent meshComponent;

    @Override
    public void render() {
        if (ImGui.beginTable("##resources" + imguiId, 2, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("Data", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Quantity", ImGuiTableColumnFlags.WidthFixed, 120f);
            ImGui.tableHeadersRow();

            render("Total triangles", meshService.getTotalTriangleCount());

            render("Triangles being rendered", renderingRepository.getTotalTriangleCount());

            render("Textures", textureService.getTotalTextureCount());

            render("Voxels", voxelRepository.getVoxelCount());

            render("Resources to be streamed in", streamingRepository.schedule.size());

            render("Resources to be streamed in", streamingRepository.schedule.size());

            render("Renderable entities", meshComponent.bag.size());

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

