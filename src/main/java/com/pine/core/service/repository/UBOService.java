package com.pine.core.service.repository;

import com.pine.core.service.common.IResource;
import com.pine.core.service.common.IResourceService;
import com.pine.core.service.repository.primitives.ubo.UBO;
import com.pine.core.service.repository.primitives.ubo.UBOCreationData;
import com.pine.core.service.repository.primitives.ubo.UBORuntimeData;
import org.lwjgl.opengl.GL46;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;

// TODO - Runtime data should be object containing new values for UBO
@Repository
public class UBOService implements IResourceService<UBO, UBORuntimeData, UBOCreationData> {
    private UBO currentUBO;

    @Override
    public void bind(UBO instance, UBORuntimeData data) {
        bind(instance);
        updateData(data);
    }

    @Override
    public void bind(UBO instance) {
        currentUBO = instance;
        bindInternal();
    }

    @Override
    public void unbind() {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }

    @Override
    public IResource add(UBOCreationData data) {
        return null;
    }

    @Override
    public void remove(UBO data) {
        GL46.glDeleteBuffers(data.getBuffer());
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
