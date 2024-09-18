package com.pine.core.service.resource.ubo;

import com.pine.core.service.resource.resource.ResourceCreationData;
import com.pine.core.service.resource.resource.ResourceType;

import java.util.List;
import java.util.Objects;

public final class UBOCreationData implements ResourceCreationData {
    private final List<UBOData> data;
    private final String blockName;

    public UBOCreationData(String blockName, UBOData ...data) {
        this.data = List.of(data);
        this.blockName = blockName;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.UBO;
    }

    public List<UBOData> data() {
        return data;
    }

    public String blockName() {
        return blockName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (UBOCreationData) obj;
        return Objects.equals(this.data, that.data) &&
                Objects.equals(this.blockName, that.blockName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, blockName);
    }

    @Override
    public String toString() {
        return "UBOCreationData[" +
                "data=" + data + ", " +
                "blockName=" + blockName + ']';
    }

}
