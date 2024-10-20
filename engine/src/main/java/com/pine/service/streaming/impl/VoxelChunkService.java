package com.pine.service.streaming.impl;

import com.pine.injection.PBean;
import com.pine.repository.streaming.AbstractResourceRef;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.streaming.AbstractStreamableService;
import com.pine.service.streaming.StreamData;
import com.pine.service.streaming.data.VoxelChunkStreamData;
import com.pine.service.streaming.ref.VoxelChunkResourceRef;
import org.lwjgl.system.MemoryUtil;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.IntBuffer;

@PBean
public class VoxelChunkService extends AbstractStreamableService<VoxelChunkResourceRef> {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.VOXEL_CHUNK;
    }

    @Override
    public StreamData stream(String pathToFile) {
        try {
            try (DataInputStream dis = new DataInputStream(new FileInputStream(pathToFile))) {
                int length = dis.readInt();  // Read the length of the array
                IntBuffer data = MemoryUtil.memAllocInt(length);
                for (int i = 0; i < length; i++) {
                    data.put(i, dis.readInt());
                }
                return new VoxelChunkStreamData(data);
            }
        } catch (Exception e) {
            getLogger().error("Error while reading voxel chunk {}", pathToFile, e);
        }
        return null;
    }

    @Override
    public AbstractResourceRef<?> newInstance(String key) {
        return new VoxelChunkResourceRef(key);
    }
}
