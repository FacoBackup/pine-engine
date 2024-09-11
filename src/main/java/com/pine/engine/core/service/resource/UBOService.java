package com.pine.engine.core.service.resource;

import com.pine.engine.Engine;
import com.pine.engine.core.service.EngineInjectable;
import com.pine.engine.core.service.resource.ubo.UBO;
import com.pine.engine.core.service.resource.ubo.UBOCreationData;
import com.pine.engine.core.service.resource.ubo.UBORuntimeData;
import com.pine.engine.core.service.resource.resource.AbstractResourceService;
import com.pine.engine.core.service.resource.resource.IResource;
import com.pine.engine.core.service.resource.resource.ResourceType;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

// TODO - Runtime data should be object containing new values for UBO
public class UBOService extends AbstractResourceService<UBO, UBORuntimeData, UBOCreationData> {
    private UBO currentUBO;

    public UBOService(Engine engine) {
        super(engine);
    }

    @Override
    protected void bindInternal(UBO instance, UBORuntimeData data) {
        bindInternal(instance);
        updateData(data);
    }

    @Override
    protected void bindInternal(UBO instance) {
        currentUBO = instance;
        bindInternal();
    }

    @Override
    public void unbind() {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }

    @Override
    protected IResource addInternal(UBOCreationData data) {
        return null;
    }

    @Override
    protected void removeInternal(UBO data) {
        GL46.glDeleteBuffers(data.getBuffer());
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.UBO;
    }

    public void bindWithShader(int shaderProgram) {
        GL46.glUseProgram(shaderProgram);
        int index = GL46.glGetUniformBlockIndex(shaderProgram, currentUBO.getBlockName());
        GL46.glUniformBlockBinding(shaderProgram, index, currentUBO.getBlockPoint());
        unbind();
    }

    private void bindInternal() {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, currentUBO.getBuffer());
    }

    public void updateData(UBORuntimeData data) {
        UBO.UBOItem item = currentUBO.getItems()
                .get(currentUBO.getKeys().indexOf(data.propertyName()));
        bindInternal();
        updateBuffer(data.newData(), item.offset());
        unbind();
    }

    public void updateBuffer(ByteBuffer data, int offset) {
        bindInternal();
        GL46.glBufferSubData(GL46.GL_UNIFORM_BUFFER, offset, data);
        unbind();
    }
}
