package com.pine.repository.rendering;

import com.pine.injection.PBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PBean
public class RenderingRepository {
    public List<RenderingRequest> requests = new ArrayList<>();
    public List<RenderingRequest> newRequests = new ArrayList<>();
    public int pendingTransformations = 0;
    public int lightCount = 0;
    public boolean infoUpdated = false;

    public int offset = 0;
    public int pendingTransformationsInternal = 0;
    public final Map<String, Boolean> auxAddedToBufferEntities = new HashMap<>();

    public void switchRequests() {
        pendingTransformations = pendingTransformationsInternal;
        List<RenderingRequest> aux = requests;
        requests = newRequests;
        newRequests = aux;
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
