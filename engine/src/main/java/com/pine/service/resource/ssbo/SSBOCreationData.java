package com.pine.service.resource.ssbo;

import com.pine.service.resource.LocalResourceType;
import com.pine.service.resource.ResourceCreationData;

import java.nio.Buffer;

public final class SSBOCreationData extends ResourceCreationData {
    private final int bindingPoint;
    private Buffer data;
    private long expectedSize;


    public SSBOCreationData(int bindingPoint, long expectedSize) {
        this.bindingPoint = bindingPoint;
        this.expectedSize = expectedSize;
    }

    public SSBOCreationData(int bindingPoint, Buffer data) {
        this.bindingPoint = bindingPoint;
        this.data = data;
    }

    public Buffer getData() {
        return data;
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
