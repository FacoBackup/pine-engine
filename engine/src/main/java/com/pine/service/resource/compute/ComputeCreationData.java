package com.pine.service.resource.compute;

import com.pine.service.resource.LocalResourceType;
import com.pine.service.resource.ResourceCreationData;
import com.pine.service.resource.shader.ShaderCreationData;

public final class ComputeCreationData extends ResourceCreationData {
    private final String code;
    private final boolean localResource;

    public ComputeCreationData(String code) {
        this.code = code;
        this.localResource = code.contains(ShaderCreationData.LOCAL_SHADER);
    }

    @Override
    public LocalResourceType getResourceType() {
        return LocalResourceType.COMPUTE;
    }

    public String code() {
        return code;
    }

    public boolean isLocalResource() {
        return localResource;
    }
}
