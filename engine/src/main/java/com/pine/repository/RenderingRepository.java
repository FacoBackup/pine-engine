package com.pine.repository;

import com.pine.PBean;
import com.pine.repository.rendering.PrimitiveRenderRequest;

import java.util.ArrayList;
import java.util.List;

@PBean
public class RenderingRepository {
    public List<PrimitiveRenderRequest> requests = new ArrayList<>();
    public List<PrimitiveRenderRequest> newRequests = new ArrayList<>();
    public int requestCount = 0;
    public int lightCount = 0;
    public boolean infoUpdated = false;

    public void switchRequests(){
        List<PrimitiveRenderRequest> aux = requests;
        requests = newRequests;
        newRequests = aux;
    }
}
