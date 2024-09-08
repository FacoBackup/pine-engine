package com.pine.engine.core.service.resource.primitives.shader;

import com.pine.engine.core.service.resource.resource.ResourceCreationData;
import com.pine.engine.core.service.resource.resource.ResourceType;

import java.util.Objects;

public final class ShaderCreationData extends ResourceCreationData {
    private final String vertex;
    private final String fragment;
    private final String absoluteId;

    public ShaderCreationData(String vertex, String fragment, String absoluteId) {
        this.vertex = vertex;
        this.fragment = fragment;
        this.absoluteId = absoluteId;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SHADER;
    }

    public String vertex() {
        return vertex;
    }

    public String fragment() {
        return fragment;
    }

    public String absoluteId() {
        return absoluteId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ShaderCreationData) obj;
        return Objects.equals(this.vertex, that.vertex) &&
                Objects.equals(this.fragment, that.fragment) &&
                Objects.equals(this.absoluteId, that.absoluteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertex, fragment, absoluteId);
    }

    @Override
    public String toString() {
        return "ShaderCreationDTO[" +
                "vertex=" + vertex + ", " +
                "fragment=" + fragment + ", " +
                "absoluteId=" + absoluteId + ']';
    }

}
