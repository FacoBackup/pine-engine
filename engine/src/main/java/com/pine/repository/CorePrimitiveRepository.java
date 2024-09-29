package com.pine.repository;

import com.pine.Engine;
import com.pine.PBean;
import com.pine.PInject;
import com.pine.service.loader.ResourceLoaderService;
import com.pine.service.loader.impl.info.MeshLoaderExtraInfo;
import com.pine.service.loader.impl.response.MeshLoaderResponse;
import com.pine.service.resource.ResourceService;
import com.pine.service.resource.primitives.mesh.MeshCreationData;
import com.pine.service.resource.primitives.mesh.Primitive;

@PBean
public class CorePrimitiveRepository implements CoreRepository {
    @PInject
    public Engine engine;
    @PInject
    public ResourceService resources;
    @PInject
    public ResourceLoaderService resourceLoader;

    public Primitive planeMesh;
    public Primitive quadMesh;
    public Primitive cubeMesh;

    @Override
    public void initialize() {
        var planeResponse = (MeshLoaderResponse) resourceLoader.load("plane.glb", true);
        if (planeResponse != null) {
            planeMesh = (Primitive) resources.getById(planeResponse.getMeshes().getFirst().id());
            resources.makeStatic(planeMesh);
        }

        var cubeResponse = (MeshLoaderResponse) resourceLoader.load("cube.glb", true);
        if (cubeResponse != null) {
            cubeMesh = (Primitive) resources.getById(cubeResponse.getMeshes().getFirst().id());
            resources.makeStatic(cubeMesh);
        }
        quadMesh = (Primitive) resources.addResource(new MeshCreationData(
                new float[]{-1, -1, (float) -4.371138828673793e-8, 1, -1, (float) -4.371138828673793e-8, -1, 1, 4.371138828673793e-8F, 1, 1, 4.371138828673793e-8F},
                new int[]{0, 1, 3, 0, 3, 2},
                null,
                null
        ).staticResource());

    }
}
