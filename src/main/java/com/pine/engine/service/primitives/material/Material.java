package com.pine.engine.service.primitives.material;


import com.pine.common.resource.AbstractResource;
import com.pine.common.resource.ResourceType;

/**
 * Collection of uniforms attributes related to a shader
 */
public class Material extends AbstractResource<MaterialDTO> {
    public Material(String id, MaterialDTO dto) {
        super(id);
        // TODO
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.MATERIAL;
    }
}
