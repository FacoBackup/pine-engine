package com.pine.service.resource.ssbo;

import com.pine.service.resource.LocalResourceType;
import com.pine.service.resource.ubo.UBOCreationData;

public final class SSBOCreationData extends UBOCreationData {
    private final int bindingPoint;
    private final long expectedSize;

    public SSBOCreationData(int bindingPoint, long expectedSize) {
        super(null);
        this.bindingPoint = bindingPoint;
        this.expectedSize = expectedSize;
    }

    public int getBindingPoint() {
        return bindingPoint;
    }

    @Override
    public LocalResourceType getResourceType() {
        return LocalResourceType.SSBO;
    }

    public long getExpectedSize() {
        return expectedSize;
    }
}
