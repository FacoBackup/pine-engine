package com.pine.repository.rendering;

import com.pine.injection.PBean;
import com.pine.service.streaming.ref.EnvironmentMapResourceRef;

import java.util.HashMap;
import java.util.Map;

import static com.pine.service.system.impl.AbstractGBufferPass.MAX_CUBE_MAPS;

@PBean
public class RenderingRepository {
    public EnvironmentMapResourceRef[] environmentMaps = new EnvironmentMapResourceRef[MAX_CUBE_MAPS];

    public int lightCount = 0;
    public boolean infoUpdated = false;

    public int offset = 0;
    public final Map<String, Boolean> auxAddedToBufferEntities = new HashMap<>();
    public int voxelChunksFilled;


}
