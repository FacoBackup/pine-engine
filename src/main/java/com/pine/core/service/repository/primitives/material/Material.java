package com.pine.core.service.repository.primitives.material;


import com.pine.core.service.ResourceType;
import com.pine.core.service.common.AbstractResource;

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
