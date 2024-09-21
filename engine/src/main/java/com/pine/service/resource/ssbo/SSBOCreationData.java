package com.pine.service.resource.ssbo;

import com.pine.service.resource.resource.ResourceCreationData;
import com.pine.service.resource.resource.ResourceType;
import com.pine.service.resource.ubo.UBOCreationData;
import com.pine.service.resource.ubo.UBOData;

import java.util.List;
import java.util.Objects;

public final class SSBOCreationData extends UBOCreationData {
    private final int bindingPoint;

    public SSBOCreationData(String blockName, int bindingPoint, SSBOData... data) {
        super(blockName, data);
        this.bindingPoint = bindingPoint;
    }

    public int getBindingPoint() {
        return bindingPoint;
    }
}
