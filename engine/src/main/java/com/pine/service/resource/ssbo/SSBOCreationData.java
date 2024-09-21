package com.pine.service.resource.ssbo;

import com.pine.service.resource.resource.ResourceType;
import com.pine.service.resource.ubo.UBOCreationData;

public final class SSBOCreationData extends UBOCreationData {
    private final int bindingPoint;
    private final long expectedSize;

    public SSBOCreationData(String blockName, int bindingPoint, long expectedSize) {
        super(blockName);
        this.bindingPoint = bindingPoint;
        this.expectedSize = expectedSize;
    }

    public int getBindingPoint() {
        return bindingPoint;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SSBO;
    }

    public long getExpectedSize() {
        return expectedSize;
    }
}
