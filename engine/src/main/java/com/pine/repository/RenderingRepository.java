package com.pine.repository;

import com.pine.PBean;
import com.pine.repository.rendering.PrimitiveRenderingRequest;

import java.util.ArrayList;
import java.util.List;

@PBean
public class RenderingRepository {
    public List<PrimitiveRenderingRequest> requests = new ArrayList<>();
}
