package com.pine.service.resource;

import com.pine.injection.EngineInjectable;
import com.pine.service.resource.primitives.EmptyRuntimeData;
import com.pine.service.resource.resource.AbstractResourceService;
import com.pine.service.resource.resource.IResource;
import com.pine.service.resource.resource.ResourceType;
import com.pine.service.resource.ubo.UBO;
import com.pine.service.resource.ubo.UBOCreationData;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;

@EngineInjectable
public class UBOService extends AbstractResourceService<UBO, EmptyRuntimeData, UBOCreationData> {
    private UBO currentUBO;

    @Override
    protected void bindInternal(UBO instance, EmptyRuntimeData data) {
        bindInternal(instance);
    }

    @Override
    protected void bindInternal(UBO instance) {
        currentUBO = instance;
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, currentUBO.getBuffer());
    }

    @Override
    public void unbind() {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, GL46.GL_NONE);
    }

    @Override
    protected IResource addInternal(UBOCreationData data) {
        return new UBO(getId(), data);
    }

    @Override
    protected void removeInternal(UBO data) {
        GL46.glDeleteBuffers(data.getBuffer());
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.UBO;
    }

    public void bindWithShader(UBO ubo, int shaderProgram) {
        bindInternal(ubo);
        GL46.glUseProgram(shaderProgram);
        int index = GL46.glGetUniformBlockIndex(shaderProgram, currentUBO.getBlockName());
        GL46.glUniformBlockBinding(shaderProgram, index, currentUBO.getBlockPoint());
        unbind();
    }

    public void updateBuffer(UBO ubo, FloatBuffer data, int offset) {
        currentUBO = ubo;
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, currentUBO.getBuffer());
        GL46.glBufferSubData(GL46.GL_UNIFORM_BUFFER, offset, data);
        unbind();
    }
}
