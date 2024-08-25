package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceRepository;
import com.pine.core.service.repository.primitives.shader.Shader;
import com.pine.core.service.repository.primitives.shader.ShaderCreationDTO;
import com.pine.core.service.repository.primitives.shader.ShaderRuntimeDTO;
import org.springframework.stereotype.Repository;

@Repository
public class ShaderRepository  implements IResourceRepository<ShaderRuntimeDTO, ShaderCreationDTO> {

    @Override
    public void bind(String id, ShaderRuntimeDTO data) {

    }

    @Override
    public void bind(String id) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public <T extends IResource> T add(ShaderCreationDTO data) {
        return null;
    }

    @Override
    public void remove(String id) {

    }
}
