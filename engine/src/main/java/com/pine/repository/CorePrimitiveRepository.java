package com.pine.repository;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.service.loader.StreamingService;
import com.pine.service.loader.impl.response.MeshLoaderResponse;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.primitives.mesh.Mesh;
import com.pine.service.resource.primitives.mesh.MeshCreationData;

@PBean
public class CorePrimitiveRepository implements CoreRepository {
    @PInject
    public Engine engine;
    @PInject
    public ResourceService resources;
    @PInject
    public StreamingService resourceLoader;

    public Mesh planeMesh;
    public Mesh quadMesh;
    public Mesh cubeMesh;

    @Override
    public void initialize() {
        var planeResponse = (MeshLoaderResponse) resourceLoader.load("plane.glb", true);
        if (planeResponse != null) {
            planeMesh = (Mesh) resources.getById(planeResponse.getMeshes().getFirst().id());
            resources.makeStatic(planeMesh);
        }

        var cubeResponse = (MeshLoaderResponse) resourceLoader.load("cube.glb", true);
        if (cubeResponse != null) {
            cubeMesh = (Mesh) resources.getById(cubeResponse.getMeshes().getFirst().id());
            resources.makeStatic(cubeMesh);
        }
        quadMesh = (Mesh) resources.addResource(new MeshCreationData(
                new float[]{-1, -1, (float) -4.371138828673793e-8, 1, -1, (float) -4.371138828673793e-8, -1, 1, 4.371138828673793e-8F, 1, 1, 4.371138828673793e-8F},
                new int[]{0, 1, 3, 0, 3, 2},
                null,
                null
        ).staticResource());

    }
}
