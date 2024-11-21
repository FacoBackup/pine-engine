package com.pine.panels.resources;

import com.pine.component.MeshComponent;
import com.pine.core.dock.AbstractDockPanel;
import com.pine.injection.PInject;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.repository.streaming.StreamingRepository;
import com.pine.repository.terrain.FoliageInstance;
import com.pine.repository.terrain.TerrainRepository;
import com.pine.service.grid.WorldService;
import com.pine.service.streaming.StreamingService;
import com.pine.service.streaming.ref.MeshResourceRef;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;

import java.util.Collection;

import static com.pine.panels.console.ConsolePanel.TABLE_FLAGS;


public class ResourcesPanel extends AbstractDockPanel {

    @PInject
    public WorldService worldService;

    @PInject
    public StreamingService streamingService;

    @PInject
    public TerrainRepository terrainRepository;

    @PInject
    public WorldRepository world;

    @PInject
    public StreamingRepository streamingRepository;

    private int totalTriangles = 0;
    private int totalTerrainTiles = 0;
    private int totalTerrainTriangles = 0;
    private int totalDrawCalls = 0;

    @Override
    public void render() {
        if (ImGui.beginTable("##resources" + imguiId, 2, TABLE_FLAGS)) {
            ImGui.tableSetupColumn("Data", ImGuiTableColumnFlags.WidthStretch);
            ImGui.tableSetupColumn("Quantity", ImGuiTableColumnFlags.WidthFixed, 120f);
            ImGui.tableHeadersRow();

            computeDrawCallQuantity();
            computeTerrainData();

            render("Individual draw calls", totalDrawCalls);

            render("Terrain tiles visible", totalTerrainTiles);

            render("Terrain triangles rendered", totalTerrainTriangles);

            int total = 0;
            for (FoliageInstance f : terrainRepository.foliage.values()) {
                total += f.count;
                if (f.mesh != null) {
                    var mesh = (MeshResourceRef) streamingService.streamIn(f.mesh, StreamableResourceType.MESH);
                    if (mesh != null) {
                        totalTriangles += mesh.triangleCount * f.count;
                    }
                }
            }
            totalTriangles += totalTerrainTiles;
            render("Foliage instances", total);

            render("Triangles being rendered", totalTriangles);

            render("Textures", getTotalTextureCount());

            render("Voxels", getVoxelCount());

            render("Resources to be streamed in", streamingRepository.toStreamIn.size());

            render("Resources to be streamed in", streamingRepository.toStreamIn.size());

            ImGui.endTable();
        }
    }

    private void computeTerrainData() {
        totalTerrainTriangles = totalTerrainTiles = 0;
        if (terrainRepository.chunks != null) {
            for (var chunk : terrainRepository.chunks) {
                if (!chunk.isCulled()) {
                    totalTerrainTriangles += chunk.getTriangles();
                    totalTerrainTiles++;
                }
            }
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

    public void computeDrawCallQuantity() {
        totalDrawCalls = totalTriangles = 0;

        for (var tile : worldService.getLoadedTiles()) {
            if (tile != null) {
                Collection<MeshComponent> meshes = world.bagMeshComponent.values();
                for (var mesh : meshes) {
                    if (worldService.isMeshReady(mesh)) {
                        totalTriangles += mesh.renderRequest.mesh.triangleCount;
                        totalDrawCalls++;
                    }
                }
            }
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

