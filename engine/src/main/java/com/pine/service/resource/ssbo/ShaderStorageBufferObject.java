package com.pine.service.resource.ssbo;

import com.pine.service.resource.resource.AbstractResource;
import com.pine.service.resource.resource.ResourceType;
import org.lwjgl.opengl.GL46;

public class ShaderStorageBufferObject extends AbstractResource {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SSBO;
    }

    private final int bindingPoint;
    private final int buffer;

    public ShaderStorageBufferObject(String id, SSBOCreationData dto) {
        super(id);
        buffer = GL46.glCreateBuffers();
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, buffer);
        GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, dto.getExpectedSize(), GL46.GL_DYNAMIC_COPY);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, GL46.GL_NONE);
        bindingPoint = dto.getBindingPoint();
    }

    public int getBuffer() {
        return buffer;
    }

    public int getBindingPoint() {
        return bindingPoint;
    }
}
