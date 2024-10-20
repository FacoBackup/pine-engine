package com.pine.repository.core;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.service.importer.data.MeshImportData;
import com.pine.service.resource.ResourceService;
import com.pine.service.streaming.ref.MeshResourceRef;

@PBean
public class CoreMeshRepository implements CoreRepository {
    @PInject
    public Engine engine;

    @PInject
    public ResourceService resources;

    public MeshResourceRef planeMesh;
    public MeshResourceRef quadMesh;
    public MeshResourceRef cubeMesh;

    @Override
    public void initialize() {
        planeMesh = new MeshResourceRef("");
        planeMesh.load(new MeshImportData(
                null,
                new float[]{-1, 0, 1, 1, 0, 1, -1, 0, -1, 1, 0, -1},
                new int[]{0, 1, 3, 0, 3, 2},
                null,
                null
        ));

        cubeMesh = new MeshResourceRef("");
        cubeMesh.load(new MeshImportData(
                null,
                new float[]{1, 1, -1, 1, 1, -1, 1, 1, -1, 1, -1, -1, 1, -1, -1, 1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, -1, 1, 1, -1, 1, 1, -1, 1, -1, 1, -1, -1, 1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, -1, 1, 1, -1, 1, 1, -1, -1, 1, -1, -1, 1, -1, -1, 1},
                new int[]{1, 14, 20, 1, 20, 7, 10, 6, 19, 10, 19, 23, 21, 18, 12, 21, 12, 15, 16, 3, 9, 16, 9, 22, 5, 2, 8, 5, 8, 11, 17, 13, 0, 17, 0, 4},
                null,
                null
        ));

        quadMesh = new MeshResourceRef("");
        quadMesh.load(new MeshImportData(
                null,
                new float[]{-1, -1, (float) -4.371138828673793e-8, 1, -1, (float) -4.371138828673793e-8, -1, 1, 4.371138828673793e-8F, 1, 1, 4.371138828673793e-8F},
                new int[]{0, 1, 3, 0, 3, 2},
                null,
                null
        ));
    }
}
