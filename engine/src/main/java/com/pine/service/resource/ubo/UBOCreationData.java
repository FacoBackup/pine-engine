package com.pine.service.resource.ubo;

import com.pine.service.resource.IResourceCreationData;

import java.util.List;

public class UBOCreationData implements IResourceCreationData {
    private final List<UBOData> data;
    private final String blockName;

    public UBOCreationData(String blockName, UBOData ...data) {
        this.data = List.of(data);
        this.blockName = blockName;
    }

    public List<UBOData> data() {
        return data;
    }

    public String blockName() {
        return blockName;
    }
}
