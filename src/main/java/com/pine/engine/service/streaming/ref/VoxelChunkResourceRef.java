package com.pine.engine.service.streaming.ref;

import com.pine.engine.repository.streaming.AbstractResourceRef;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.resource.ssbo.SSBO;
import com.pine.engine.service.resource.ssbo.SSBOCreationData;
import com.pine.engine.service.streaming.data.VoxelChunkStreamData;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public class VoxelChunkResourceRef extends AbstractResourceRef<VoxelChunkStreamData> {
    private SSBO buffer;
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
        buffer = new SSBO(new SSBOCreationData(data.buffer()));
        MemoryUtil.memFree(data.buffer());
    }

    public int getQuantity() {
        return quantity;
    }

    public SSBO getBuffer() {
        return buffer;
    }

    @Override
    protected void disposeInternal() {
        if (buffer != null) {
            buffer.dispose();
        }
    }
}
