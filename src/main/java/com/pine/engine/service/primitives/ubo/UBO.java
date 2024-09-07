package com.pine.engine.service.primitives.ubo;

import com.pine.engine.resource.AbstractResource;
import com.pine.engine.resource.ResourceType;
import com.pine.engine.service.primitives.GLSLType;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

public class UBO extends AbstractResource<UBOCreationData> {
    private static int blockPointIncrement = 0;

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

    public int getBuffer() {
        return buffer;
    }

    public static int getBlockPointIncrement() {
        return blockPointIncrement;
    }

    public List<UBOItem> getItems() {
        return items;
    }

    public List<String> getKeys() {
        return keys;
    }

    public String getBlockName() {
        return blockName;
    }

    public int getBlockPoint() {
        return blockPoint;
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
