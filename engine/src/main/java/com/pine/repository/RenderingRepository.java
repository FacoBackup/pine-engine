package com.pine.repository;

import com.pine.PBean;
import com.pine.repository.rendering.PrimitiveRenderRequest;

import java.util.ArrayList;
import java.util.List;

@PBean
public class RenderingRepository {
    public List<PrimitiveRenderRequest> requests = new ArrayList<>();
    public int requestCount = 0;
    public int lightCount = 0;
    public boolean infoUpdated = false;
}
