package com.pine.repository.rendering;

import com.pine.injection.PBean;
import com.pine.service.streaming.ref.EnvironmentMapResourceRef;
import com.pine.service.streaming.ref.VoxelChunkResourceRef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PBean
public class RenderingRepository {
    public List<RenderingRequest> requests = new ArrayList<>();
    public final Map<String, RenderingRequest> newToBeRendered = new HashMap<>();
    public final Map<String, RenderingRequest> toBeRendered = new HashMap<>();
    public List<RenderingRequest> newRequests = new ArrayList<>();

    public VoxelChunkResourceRef[] voxelChunks = new VoxelChunkResourceRef[4];
    public VoxelChunkResourceRef[] newVoxelChunks = new VoxelChunkResourceRef[4];

    public EnvironmentMapResourceRef[] environmentMaps = new EnvironmentMapResourceRef[3];

    public int pendingTransformations = 0;
    public int lightCount = 0;
    public boolean infoUpdated = false;

    public int offset = 0;
    public int pendingTransformationsInternal = 0;
    public final Map<String, Boolean> auxAddedToBufferEntities = new HashMap<>();
    public int voxelChunksFilled;

    public void switchRequests() {
        pendingTransformations = pendingTransformationsInternal;
        var aux = requests;
        requests = newRequests;
        newRequests = aux;
        aux.clear();

        var auxV = voxelChunks;
        voxelChunks = newVoxelChunks;
        newVoxelChunks = auxV;

        toBeRendered.clear();
        toBeRendered.putAll(newToBeRendered);
        newToBeRendered.clear();
    }

    public int getTotalTriangleCount() {
        int total = 0;
        for (RenderingRequest request : requests) {
            if (request.transformations.isEmpty()) {
                total += request.mesh.triangleCount;
            } else {
                total += request.mesh.triangleCount * request.transformations.size();
            }
        }
        return total;
    }
}
