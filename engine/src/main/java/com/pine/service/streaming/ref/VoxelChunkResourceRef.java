package com.pine.service.streaming.ref;

import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.resource.ssbo.SSBOCreationData;
import com.pine.service.resource.ssbo.ShaderStorageBufferObject;
import com.pine.service.streaming.data.VoxelChunkStreamData;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public class VoxelChunkResourceRef extends AbstractResourceRef<VoxelChunkStreamData> {
    private ShaderStorageBufferObject buffer;
    private int quantity;
    public Vector3f center;
    public int size;
    public int depth;

    public VoxelChunkResourceRef(String id) {
        super(id);
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.VOXEL_CHUNK;
    }

    @Override
    protected void loadInternal(VoxelChunkStreamData data) {
        this.quantity = data.buffer().limit();
        buffer = new ShaderStorageBufferObject(null, new SSBOCreationData(-1, data.buffer()));
        MemoryUtil.memFree(data.buffer());
    }

    public int getQuantity() {
        return quantity;
    }

    public ShaderStorageBufferObject getBuffer() {
        return buffer;
    }

    @Override
    protected void disposeInternal() {
        if (buffer != null) {
            buffer.dispose();
        }
    }
}
