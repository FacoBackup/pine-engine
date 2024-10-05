package com.pine.repository.core;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.streaming.MeshStreamableResource;
import com.pine.service.loader.LoaderService;
import com.pine.service.resource.ResourceService;
import com.pine.service.streaming.mesh.MeshStreamData;

@PBean
public class CoreMeshRepository implements CoreRepository {
    @PInject
    public Engine engine;

    @PInject
    public ResourceService resources;

    public MeshStreamableResource planeMesh;
    public MeshStreamableResource quadMesh;

    @Override
    public void initialize() {
        planeMesh = new MeshStreamableResource("", "");
        planeMesh.load(new MeshStreamData(
                new float[]{-1, 0, 1, 1, 0, 1, -1, 0, -1, 1, 0, -1},
                new int[]{0, 1, 3, 0, 3, 2},
                null,
                null
        ));
        quadMesh = new MeshStreamableResource("", "");
        quadMesh.load(new MeshStreamData(
                new float[]{-1, -1, (float) -4.371138828673793e-8, 1, -1, (float) -4.371138828673793e-8, -1, 1, 4.371138828673793e-8F, 1, 1, 4.371138828673793e-8F},
                new int[]{0, 1, 3, 0, 3, 2},
                null,
                null
        ));
    }
}
