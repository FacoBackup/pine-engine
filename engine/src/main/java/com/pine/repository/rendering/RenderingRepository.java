package com.pine.repository.rendering;

import com.pine.component.MeshComponent;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.EngineRepository;
import com.pine.service.grid.HashGridService;
import com.pine.service.streaming.ref.EnvironmentMapResourceRef;

import java.util.*;

@PBean
public class RenderingRepository {
    public EnvironmentMapResourceRef[] environmentMaps = new EnvironmentMapResourceRef[3];

    public int lightCount = 0;
    public boolean infoUpdated = false;

    public int offset = 0;
    public final Map<String, Boolean> auxAddedToBufferEntities = new HashMap<>();
    public int voxelChunksFilled;


}
