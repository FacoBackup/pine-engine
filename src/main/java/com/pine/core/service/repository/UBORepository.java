package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceRepository;
import com.pine.core.service.repository.primitives.EmptyRuntimeData;
import com.pine.core.service.repository.primitives.ubo.UBO;
import com.pine.core.service.repository.primitives.ubo.UBOCreationData;
import org.lwjgl.opengl.GL46;
import org.springframework.stereotype.Repository;

// TODO - Runtime data should be object containing new values for UBO
@Repository
public class UBORepository implements IResourceRepository<UBO, EmptyRuntimeData, UBOCreationData> {

    @Override
    public void bind(UBO instance, EmptyRuntimeData data) {

    }

    @Override
    public void bind(UBO instance) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public IResource add(UBOCreationData data) {
        return null;
    }

    @Override
    public void remove(UBO data) {
        GL46.glDeleteBuffers(data.getBuffer());
    }
}
