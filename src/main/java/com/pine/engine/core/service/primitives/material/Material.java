package com.pine.engine.core.service.primitives.material;


import com.pine.engine.core.resource.AbstractResource;
import com.pine.engine.core.resource.ResourceType;

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
