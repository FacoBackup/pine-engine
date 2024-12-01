package com.pine.engine.service.resource.ubo;

import com.pine.common.injection.PBean;
import com.pine.engine.service.resource.AbstractResourceService;
import com.pine.engine.service.resource.shader.GLSLType;
import org.lwjgl.opengl.GL46;

import java.nio.FloatBuffer;
import java.util.List;

@PBean
public class UBOService extends AbstractResourceService<UBO, UBOCreationData> {
    private UBO currentUBO;

    @Override
    public void bind(UBO instance) {
        currentUBO = instance;
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, currentUBO.getBuffer());
    }

    @Override
    protected UBO createInternal(UBOCreationData data) {
        return new UBO(data);
    }

    @Override
    public void unbind() {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, GL46.GL_NONE);
    }

    public void bindWithShader(UBO ubo, int shaderProgram) {
        bind(ubo);
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

            offset += size[1];
        }

        return offset;
    }
}
