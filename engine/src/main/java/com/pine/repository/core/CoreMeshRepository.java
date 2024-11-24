package com.pine.repository.core;

import com.pine.Engine;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.service.importer.data.MeshImportData;
import com.pine.service.module.Initializable;
import com.pine.service.streaming.ref.MeshResourceRef;

@PBean
public class CoreMeshRepository implements Initializable {
    @PInject
    public Engine engine;

    public MeshResourceRef quadMesh;
    public MeshResourceRef cubeMesh;

    @Override
    public void onInitialize() {
        cubeMesh = new MeshResourceRef("");
        cubeMesh.load(new MeshImportData(
                null,
                new float[]{1.0f, 0.99999976f, -1.0f, -1.0f, 0.99999976f, -1.0f, -1.0f, 1.0f, 0.99999976f, 1.0f, 1.0f, 0.99999976f, 1.0f, -0.99999976f, 1.0f, 1.0f, 1.0f, 0.99999976f, -1.0f, 1.0f, 0.99999976f, -1.0f, -0.99999976f, 1.0f, -1.0f, -0.99999976f, 1.0f, -1.0f, 1.0f, 0.99999976f, -1.0f, 0.99999976f, -1.0f, -1.0f, -1.0f, -0.99999976f, -1.0f, -1.0f, -0.99999976f, 1.0f, -1.0f, -0.99999976f, 1.0f, -0.99999976f, 1.0f, -1.0f, -0.99999976f, 1.0f, 1.0f, -1.0f, -0.99999976f, 1.0f, 0.99999976f, -1.0f, 1.0f, 1.0f, 0.99999976f, 1.0f, -0.99999976f, 1.0f, -1.0f, -1.0f, -0.99999976f, -1.0f, 0.99999976f, -1.0f, 1.0f, 0.99999976f, -1.0f, 1.0f, -1.0f, -0.99999976f},
                new int[]{0, 1, 2, 0, 2, 3, 4, 5, 6, 4, 6, 7, 8, 9, 10, 8, 10, 11, 12, 13, 14, 12, 14, 15, 16, 17, 18, 16, 18, 19, 20, 21, 22, 20, 22, 23},
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
