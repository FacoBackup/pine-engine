package com.pine.repository.core;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.streaming.MeshStreamableResource;
import com.pine.service.loader.LoaderService;
import com.pine.service.resource.ResourceService;

@PBean
public class CoreMeshRepository implements CoreRepository {
    @PInject
    public Engine engine;
    @PInject
    public ResourceService resources;
    @PInject
    public LoaderService resourceLoader;

    public MeshStreamableResource planeMesh;
    public MeshStreamableResource quadMesh;
    public MeshStreamableResource cubeMesh;

    @Override
    public void initialize() {
//        var planeResponse = (MeshLoaderResponse) resourceLoader.load("plane.glb", true);
//        if (planeResponse != null) {
//            planeMesh = (MeshStreamableResource) resources.getById(planeResponse.getMeshes().getFirst().id());
//            resources.makeStatic(planeMesh);
//        }
//
//        var cubeResponse = (MeshLoaderResponse) resourceLoader.load("cube.glb", true);
//        if (cubeResponse != null) {
//            cubeMesh = (Mesh) resources.getById(cubeResponse.getMeshes().getFirst().id());
//            resources.makeStatic(cubeMesh);
//        }
//        quadMesh = (Mesh) resources.addResource(new MeshCreationData(
//                new float[]{-1, -1, (float) -4.371138828673793e-8, 1, -1, (float) -4.371138828673793e-8, -1, 1, 4.371138828673793e-8F, 1, 1, 4.371138828673793e-8F},
//                new int[]{0, 1, 3, 0, 3, 2},
//                null,
//                null
//        ).staticResource());

    }
}
