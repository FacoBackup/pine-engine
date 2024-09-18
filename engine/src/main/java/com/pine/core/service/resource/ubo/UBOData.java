package com.pine.core.service.resource.ubo;

import com.pine.core.service.resource.primitives.GLSLType;

public class UBOData {
    private final String name;
    private final GLSLType type;
    private Integer offset;
    private Integer dataSize;
    private Integer chunkSize;
    private Integer dataLength;

    private UBOData(String name, GLSLType type) {
        this.name = name;
        this.type = type;
    }

    public static UBOData of(String name, GLSLType type) {
        return new UBOData(name, type);
    }

    public static UBOData of(String name, GLSLType type, int dataLength) {
        var ubo = of(name, type);
        ubo.setDataLength(dataLength);
        return ubo;
    }

    public String getName() {
        return name;
    }

    public GLSLType getType() {
        return type;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getDataSize() {
        return dataSize;
    }

    public void setDataSize(Integer dataSize) {
        this.dataSize = dataSize;
    }

    public Integer getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Integer getDataLength() {
        return dataLength;
    }

    public void setDataLength(Integer dataLength) {
        this.dataLength = dataLength;
    }
}
