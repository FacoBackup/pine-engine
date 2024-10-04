package com.pine.service.resource.ubo;

import com.pine.service.resource.LocalResourceType;
import com.pine.service.resource.ResourceCreationData;

import java.util.List;

public class UBOCreationData extends ResourceCreationData {
    private final List<UBOData> data;
    private final String blockName;

    public UBOCreationData(String blockName, UBOData ...data) {
        this.data = List.of(data);
        this.blockName = blockName;
    }

    @Override
    public LocalResourceType getResourceType() {
        return LocalResourceType.UBO;
    }

    public List<UBOData> data() {
        return data;
    }

    public String blockName() {
        return blockName;
    }
}
