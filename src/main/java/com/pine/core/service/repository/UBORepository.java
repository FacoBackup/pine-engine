package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceRepository;
import com.pine.core.service.repository.primitives.EmptyRuntimeData;
import com.pine.core.service.repository.primitives.ubo.UBO;
import com.pine.core.service.repository.primitives.ubo.UBOCreationData;
import org.springframework.stereotype.Repository;

// TODO - Runtime data should be object containing new values for UBO
@Repository
public class UBORepository implements IResourceRepository<EmptyRuntimeData, UBOCreationData> {

    @Override
    public void bind(String id, EmptyRuntimeData data) {

    }

    @Override
    public void bind(String id) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public <T extends IResource> T add(UBOCreationData data) {
        return null;
    }

    @Override
    public void remove(String id) {

    }
}
