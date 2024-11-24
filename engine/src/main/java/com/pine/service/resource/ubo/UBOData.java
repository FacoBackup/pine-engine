package com.pine.service.resource.ubo;

import com.pine.service.resource.shader.GLSLType;

public class UBOData {
    private final String name;
    private final GLSLType type;
    private Integer offset;
    private Integer chunkSize;
    private final Integer dataLength;

    public UBOData(String name, GLSLType type) {
        this(name, type, null);
    }

    public UBOData(String name, GLSLType type, Integer dataLength) {
        this.name = name;
        this.type = type;
        this.dataLength = dataLength == null ? 0 : dataLength;
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

    public Integer getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Integer getDataLength() {
        return dataLength;
    }
}
