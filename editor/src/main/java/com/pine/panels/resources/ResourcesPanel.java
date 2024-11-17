package com.pine.panels.resources;

import com.pine.component.MeshComponent;
import com.pine.core.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.repository.EngineRepository;
import com.pine.repository.FoliageInstance;
import com.pine.repository.TerrainRepository;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.service.grid.WorldService;
import com.pine.service.streaming.impl.MeshService;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;

import java.util.Collection;

import static com.pine.panels.console.ConsolePanel.TABLE_FLAGS;


public class ResourcesPanel extends AbstractDockPanel {

    @PInject
    public WorldService worldService;

    @PInject
    public EngineRepository engineRepository;

    @PInject
    public TerrainRepository terrainRepository;

    @PInject
    public WorldRepository world;

    @PInject
    public StreamingRepository streamingRepository;

    private int totalTriangles = 0;
    private int totalTerrainTiles = 0;
    private int totalTerrainTriangles = 0;

    @Override
    public void render() {
        if (ImGui.beginTable("##resources" + imguiId, 2, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("Data", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Quantity", ImGuiTableColumnFlags.WidthFixed, 120f);
            ImGui.tableHeadersRow();

            render("Individual draw calls", getDrawCallQuantity());

            render("Terrain tiles visible", totalTerrainTiles);

            render("Terrain triangles rendered", totalTerrainTriangles);

            int total = 0;
            for (FoliageInstance f : terrainRepository.foliage.values()) {
                total += f.count;
            }
            render("Foliage instances", total);

            render("Triangles being rendered", getTotalTriangleCount());

            render("Textures", getTotalTextureCount());

            render("Voxels", getVoxelCount());

            render("Resources to be streamed in", streamingRepository.toStreamIn.size());

            render("Resources to be streamed in", streamingRepository.toStreamIn.size());

            ImGui.endTable();
        }
    }

    public int getVoxelCount() {
        var svo = worldService.getCurrentTile().getSvo();
        if (svo != null) {
            return svo.getNodeQuantity();
        }
        return 0;
    }

    public int getTotalTextureCount() {
        int total = 0;
        for (AbstractResourceRef<?> resourceRef : streamingRepository.streamed.values()) {
            if (resourceRef.isLoaded() && resourceRef.getResourceType() == StreamableResourceType.TEXTURE) {
                total++;
            }
        }
        return total;
    }

    public int getTotalTriangleCount() {
        // TODO - INCLUDE FOLIAGE
        return totalTriangles;
    }

    public int getDrawCallQuantity() {
        int totalDrawCalls = totalTriangles = 0;

        for (var tile : worldService.getLoadedTiles()) {
            if (tile != null) {
                Collection<MeshComponent> meshes = world.bagMeshComponent.values();
                for (var mesh : meshes) {
                    if (mesh.canRender(engineRepository.disableCullingGlobally, world.hiddenEntityMap)) {
                        totalTriangles += mesh.renderRequest.mesh.triangleCount;
                        totalDrawCalls++;
                    }
                }
            }
        }

        return totalDrawCalls;
    }

    private void render(String Resources_to_be_streamed_in, int schedule) {
        ImGui.tableNextRow();
        ImGui.tableNextColumn();
        ImGui.text(Resources_to_be_streamed_in);
        ImGui.tableNextColumn();
        ImGui.text(String.valueOf(schedule));
    }
}

