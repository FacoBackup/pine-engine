package com.pine.core.service.repository.primitives.ubo;

import com.pine.core.service.ResourceType;
import com.pine.core.service.common.AbstractResource;
import com.pine.core.service.repository.primitives.GLSLType;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class UBO extends AbstractResource<UBOCreationData> {
    @Override
    public ResourceType getResourceType() {
        return ResourceType.UBO;
    }

    public record UBOItem(
            int offset,
            int dataSize,
            int chunkSize
    ) {
    }

    private final List<UBOItem> items = new ArrayList<>();
    private final List<String> keys = new ArrayList<>();
    private final int buffer;
    private final String blockName;
    private final int blockPoint;

    private static int blockPointIncrement = 0;

    public UBO(String id, UBOCreationData dto) {
        super(id);
        int bufferSize = calculate(dto.data());
        for (int i = 0; i < dto.data().size(); i++) {
            UBOData data = dto.data().get(i);
            items.add(new UBOItem(data.getOffset(), data.getDataSize(), data.getChunkSize()));
            keys.add(data.getName());
        }

        this.blockName = dto.blockName();
        this.blockPoint = blockPointIncrement++;
        buffer = GL46.glCreateBuffers();
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, buffer);
        GL46.glBufferData(GL46.GL_UNIFORM_BUFFER, bufferSize, GL46.GL_DYNAMIC_DRAW);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
        GL46.glBindBufferBase(GL46.GL_UNIFORM_BUFFER, this.blockPoint, buffer);
    }

    public void bindWithShader(int shaderProgram) {
        GL46.glUseProgram(shaderProgram);
        int index = GL46.glGetUniformBlockIndex(shaderProgram, blockName);
        GL46.glUniformBlockBinding(shaderProgram, index, this.blockPoint);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }

    public void bind() {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, buffer);
    }

    public void unbind() {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }


    public void updateData(String name, ByteBuffer data) {
        UBOItem item = items.get(keys.indexOf(name));
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, buffer);
        GL46.glBufferSubData(GL46.GL_UNIFORM_BUFFER, item.offset, data);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }

    public void updateBuffer(ByteBuffer data) {
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, buffer);
        GL46.glBufferSubData(GL46.GL_UNIFORM_BUFFER, 0, data);
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, 0);
    }

    private static int calculate(List<UBOData> dataArray) {
        int chunk = 16;
        int offset = 0;
        int[] size;

        for (int i = 0; i < dataArray.size(); i++) {
            UBOData data = dataArray.get(i);

            if (data.getDataLength() == null || data.getDataLength() == 0) {
                size = data.getType().getSizes();
            } else {
                size = new int[]{data.getDataLength() * 16 * 4, data.getDataLength() * 16 * 4};
            }

            int tsize = chunk - size[0];

            if (tsize < 0 && chunk < 16) {
                offset += chunk;
                if (i > 0) {
                    UBOData current = dataArray.get(i - 1);
                    current.setChunkSize(current.getChunkSize() + chunk);
                }
                chunk = 16;
            } else if (tsize == 0) {
                if (data.getType() == GLSLType.vec3 && chunk == 16) {
                    chunk -= size[1];
                } else {
                    chunk = 16;
                }
            } else if (tsize >= 0 || chunk != 16) {
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