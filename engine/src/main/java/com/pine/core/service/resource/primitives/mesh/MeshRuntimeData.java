package com.pine.core.service.resource.primitives.mesh;

import com.pine.core.service.resource.resource.IResourceRuntimeData;

public final class MeshRuntimeData implements IResourceRuntimeData {
    public final MeshRenderingMode mode;
    public int instanceCount;

    public MeshRuntimeData(MeshRenderingMode mode) {
        this(mode, 0);
    }

    public MeshRuntimeData(MeshRenderingMode mode, int instanceCount) {
        this.mode = mode;
        this.instanceCount = instanceCount;
    }
}
