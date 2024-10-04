package com.pine.service.resource;

import com.pine.injection.PBean;
import com.pine.service.resource.shader.GLSLType;
import com.pine.service.resource.ubo.UBOCreationData;
import com.pine.service.resource.ubo.UBOData;
import com.pine.service.resource.ubo.UniformBufferObject;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.util.List;

@PBean
public class UBOService extends AbstractResourceService<UniformBufferObject, UBOCreationData> {
    private UniformBufferObject currentUBO;

    @Override
    protected void bindInternal(UniformBufferObject instance) {
        currentUBO = instance;
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, currentUBO.getBuffer());
    }

    @Override
    public void unbind() {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, GL46.GL_NONE);
    }

    @Override
    protected IResource addInternal(UBOCreationData data) {
        return new UniformBufferObject(getId(), data);
    }

    @Override
    protected void removeInternal(UniformBufferObject data) {
        GL46.glDeleteBuffers(data.getBuffer());
    }

    @Override
    public LocalResourceType getResourceType() {
        return LocalResourceType.UBO;
    }

    public void bindWithShader(UniformBufferObject ubo, int shaderProgram) {
        bindInternal(ubo);
        GL46.glUseProgram(shaderProgram);
        int index = GL46.glGetUniformBlockIndex(shaderProgram, currentUBO.getBlockName());
        GL46.glUniformBlockBinding(shaderProgram, index, currentUBO.getBlockPoint());
        unbind();
    }

    public void updateBuffer(UniformBufferObject ubo, FloatBuffer data, int offset) {
        currentUBO = ubo;
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, currentUBO.getBuffer());
        GL46.glBufferSubData(GL46.GL_UNIFORM_BUFFER, offset, data);
        unbind();
    }

    public static int calculateAllocation(List<UBOData> dataArray) {
        final int CHUNK_SIZE = 16;
        int chunk = CHUNK_SIZE;
        int offset = 0;
        int[] size;

        for (int i = 0; i < dataArray.size(); i++) {
            UBOData data = dataArray.get(i);

            if (data.getDataLength() == null || data.getDataLength() == 0) {
                size = data.getType().getSizes();
            } else {
                int maxSize = data.getDataLength() * CHUNK_SIZE * 4;
                size = new int[]{maxSize, maxSize};
            }

            int tsize = chunk - size[0];

            if (tsize < 0 && chunk < CHUNK_SIZE) {
                offset += chunk;
                UBOData current = dataArray.get(i - 1);
                current.setChunkSize(current.getChunkSize() + chunk);
                chunk = CHUNK_SIZE;
            } else if (tsize == 0) {
                if (data.getType() == GLSLType.VEC_3 && chunk == CHUNK_SIZE) {
                    chunk -= size[1];
                } else {
                    chunk = CHUNK_SIZE;
                }
            } else if (tsize >= 0 || chunk != CHUNK_SIZE) {
                chunk -= size[1];
            }

            data.setOffset(offset);
            data.setChunkSize(size[1]);
            data.setDataSize(size[1]);

            offset += size[1];
        }

        return offset;
    }
}
