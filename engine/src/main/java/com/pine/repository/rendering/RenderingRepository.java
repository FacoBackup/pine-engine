package com.pine.repository.rendering;

import com.pine.component.MeshComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.EngineSettingsRepository;
import com.pine.service.grid.HashGridService;
import com.pine.service.streaming.ref.EnvironmentMapResourceRef;
import com.pine.service.streaming.ref.VoxelChunkResourceRef;

import java.util.*;

@PBean
public class RenderingRepository {
    public List<RenderingRequest> requests = new ArrayList<>();

    public VoxelChunkResourceRef[] voxelChunks = new VoxelChunkResourceRef[4];
    public VoxelChunkResourceRef[] newVoxelChunks = new VoxelChunkResourceRef[4];

    public EnvironmentMapResourceRef[] environmentMaps = new EnvironmentMapResourceRef[3];

    public int lightCount = 0;
    public boolean infoUpdated = false;

    public int offset = 0;
    public final Map<String, Boolean> auxAddedToBufferEntities = new HashMap<>();
    public int voxelChunksFilled;
    private int totalTriangles = 0;
    private int totalDrawCalls = 0;

    @PInject
    public transient HashGridService hashGridService;

    @PInject
    public transient EngineSettingsRepository engineSettingsRepository;

    public void sync() {
        var auxV = voxelChunks;
        voxelChunks = newVoxelChunks;
        newVoxelChunks = auxV;
    }

    public int getTotalTriangleCount() {
        // TODO - INCLUDE FOLIAGE
        return totalTriangles;
    }

    public int getDrawCallQuantity() {
        totalTriangles = totalDrawCalls = 0;

        for(var tile : hashGridService.getLoadedTiles()){
            if(tile != null){
                Collection<MeshComponent> meshes = tile.getWorld().bagMeshComponent.values();
                for (var mesh : meshes) {
                    if (mesh.canRender(engineSettingsRepository.disableCullingGlobally, tile.getWorld().hiddenEntityMap)) {
                        totalTriangles += mesh.renderRequest.mesh.triangleCount;
                        totalDrawCalls++;
                    }
                }
            }
        }

        return totalDrawCalls;
    }
}
