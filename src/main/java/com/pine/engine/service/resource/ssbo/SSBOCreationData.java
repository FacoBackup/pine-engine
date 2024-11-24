package com.pine.engine.service.resource.ssbo;

import com.pine.engine.service.resource.IResourceCreationData;

import java.nio.Buffer;

public final class SSBOCreationData implements IResourceCreationData {
    private final int bindingPoint;
    private Buffer data;
    private long expectedSize;


    public SSBOCreationData(int bindingPoint, long expectedSize) {
        this.bindingPoint = bindingPoint;
        this.expectedSize = expectedSize;
    }

    public SSBOCreationData(Buffer data) {
        this.bindingPoint = 0;
        this.data = data;
    }

    public Buffer getData() {
        return data;
    }

    public int getBindingPoint() {
        return bindingPoint;
    }

    public long getExpectedSize() {
        return expectedSize;
    }
}
