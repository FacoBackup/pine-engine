package com.pine.service.resource.ubo;

public class UBOItem {
    private final int offset;
    private final int dataSize;
    private final int chunkSize;

    public UBOItem(
            int offset,
            int dataSize,
            int chunkSize) {
        this.offset = offset;
        this.dataSize = dataSize;
        this.chunkSize = chunkSize;
    }

    public int offset() {
        return offset;
    }

    public int dataSize() {
        return dataSize;
    }

    public int chunkSize() {
        return chunkSize;
    }
}
