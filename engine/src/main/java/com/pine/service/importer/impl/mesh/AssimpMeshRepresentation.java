package com.pine.service.importer.impl.mesh;

import java.util.ArrayList;
import java.util.List;

public class AssimpMeshRepresentation {
    public final List<AssimpMeshRepresentation> children = new ArrayList<>();
    public final AssimpMeshRepresentation parent;
    public Integer primitive;
    public String name;

    public AssimpMeshRepresentation(AssimpMeshRepresentation parent) {
        this.parent = parent;
    }
}
