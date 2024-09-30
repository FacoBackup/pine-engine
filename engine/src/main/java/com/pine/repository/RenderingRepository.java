package com.pine.repository;

import com.pine.PBean;
import com.pine.repository.rendering.PrimitiveRenderRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PBean
public class RenderingRepository {
    public List<PrimitiveRenderRequest> requests = new ArrayList<>();
    public List<PrimitiveRenderRequest> newRequests = new ArrayList<>();
    public int pendingTransformations = 0;
    public int lightCount = 0;
    public boolean infoUpdated = false;

    public int offset = 0;
    public int pendingTransformationsInternal = 0;
    public final Map<String, Boolean> auxAddedToBufferEntities = new HashMap<>();

    public void switchRequests(){
        pendingTransformations = pendingTransformationsInternal;
        List<PrimitiveRenderRequest> aux = requests;
        requests = newRequests;
        newRequests = aux;
    }
}
