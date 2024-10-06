package com.pine.component;

import com.pine.inspection.MutableField;
import com.pine.repository.streaming.MeshStreamableResource;
import com.pine.theme.Icons;

public class VoxelizedMeshComponent extends AbstractComponent<VoxelizedMeshComponent>{

    @MutableField(label = "Mesh")
    public MeshStreamableResource mesh;

    @Override
    public String getTitle() {
        return "Voxelized Mesh Component";
    }

    @Override
    public String getIcon() {
        return Icons.apps;
    }
}
