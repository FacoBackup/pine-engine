package com.pine.service.resource.ubo;

import com.pine.service.resource.AbstractResource;
import com.pine.service.resource.LocalResourceType;
import com.pine.service.resource.UBOService;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

public class UniformBufferObject extends AbstractResource {
    private static int blockPointIncrement = 0;

    @Override
    public LocalResourceType getResourceType() {
        return LocalResourceType.UBO;
    }

    private final List<UBOItem> items = new ArrayList<>();
    private final List<String> keys = new ArrayList<>();
    private final int buffer;
    private final String blockName;
    private final int blockPoint;

    public UniformBufferObject(String id, UBOCreationData dto) {
        super(id);
        int bufferSize = UBOService.calculateAllocation(dto.data());
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
        GL46.glBindBuffer(GL46.GL_UNIFORM_BUFFER, GL46.GL_NONE);
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

    @Override
    public void dispose() {
        GL46.glDeleteBuffers(buffer);
    }
}
