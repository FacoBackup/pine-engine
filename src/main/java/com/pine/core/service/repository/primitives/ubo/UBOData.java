package com.pine.core.service.repository.primitives.ubo;

import com.pine.core.service.repository.primitives.GLSLType;

public class UBOData {
    private String name;
    private GLSLType type;
    private Integer offset;
    private Integer dataSize;
    private Integer chunkSize;
    private Integer dataLength;

    public UBOData(String name, GLSLType type, Integer offset, Integer dataSize, Integer chunkSize, Integer dataLength) {
        this.name = name;
        this.type = type;
        this.offset = offset;
        this.dataSize = dataSize;
        this.chunkSize = chunkSize;
        this.dataLength = dataLength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GLSLType getType() {
        return type;
    }

    public void setType(GLSLType type) {
        this.type = type;
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
