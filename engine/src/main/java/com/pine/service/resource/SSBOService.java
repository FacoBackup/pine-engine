package com.pine.service.resource;

import com.pine.injection.PBean;
import com.pine.service.resource.primitives.EmptyRuntimeData;
import com.pine.service.resource.resource.AbstractResourceService;
import com.pine.service.resource.resource.IResource;
import com.pine.service.resource.resource.ResourceType;
import com.pine.service.resource.ssbo.SSBOCreationData;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;

@PBean
public class SSBOService extends AbstractResourceService<ShaderStorageBufferObject, EmptyRuntimeData, SSBOCreationData> {

    @Override
    protected void bindInternal(ShaderStorageBufferObject instance, EmptyRuntimeData data) {
        bindInternal(instance);
    }

    @Override
    protected void bindInternal(ShaderStorageBufferObject instance) {
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, instance.getBindingPoint(), instance.getBuffer());
    }

    @Override
    public void unbind() {
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, GL46.GL_NONE);
    }

    @Override
    protected IResource addInternal(SSBOCreationData data) {
        return new ShaderStorageBufferObject(getId(), data);
    }

    @Override
    protected void removeInternal(ShaderStorageBufferObject data) {
        GL46.glDeleteBuffers(data.getBuffer());
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SSBO;
    }

    public void updateBuffer(ShaderStorageBufferObject ssbo, FloatBuffer data, int offset) {
        bindInternal(ssbo);
        GL46.glBufferSubData(GL46.GL_SHADER_STORAGE_BUFFER, offset, data);
        unbind();
    }
}
