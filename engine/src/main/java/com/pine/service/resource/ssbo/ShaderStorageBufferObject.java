package com.pine.service.resource.ssbo;

import com.pine.service.resource.UBOService;
import com.pine.service.resource.resource.AbstractResource;
import com.pine.service.resource.resource.ResourceType;
import com.pine.service.resource.ubo.UBOCreationData;
import com.pine.service.resource.ubo.UBOData;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

public class ShaderStorageBufferObject extends AbstractResource {

    @Override
    public ResourceType getResourceType() {
        return ResourceType.SSBO;
    }

    private final List<SSBOItem> items = new ArrayList<>();
    private final List<String> keys = new ArrayList<>();
    private final int buffer;
    private final String blockName;

    public ShaderStorageBufferObject(String id, SSBOCreationData dto) {
        super(id);
        int bufferSize = UBOService.calculateAllocation(dto.data());
        for (int i = 0; i < dto.data().size(); i++) {
            UBOData data = dto.data().get(i);
            items.add(new SSBOItem(data.getOffset(), data.getDataSize(), data.getChunkSize()));
            keys.add(data.getName());
        }

        this.blockName = dto.blockName();
        buffer = GL46.glCreateBuffers();
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, buffer);
        GL46.glBufferData(GL46.GL_SHADER_STORAGE_BUFFER, bufferSize, GL46.GL_DYNAMIC_COPY);
        GL46.glBindBuffer(GL46.GL_SHADER_STORAGE_BUFFER, 0);
        GL46.glBindBufferBase(GL46.GL_SHADER_STORAGE_BUFFER, dto.getBindingPoint(), buffer);
    }

    public int getBuffer() {
        return buffer;
    }

    public List<SSBOItem> getItems() {
        return items;
    }

    public List<String> getKeys() {
        return keys;
    }

    public String getBlockName() {
        return blockName;
    }
}
