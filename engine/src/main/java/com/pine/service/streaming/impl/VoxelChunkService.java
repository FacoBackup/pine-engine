package com.pine.service.streaming.impl;

import com.pine.FSUtil;
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
        var dataRaw = (int[]) FSUtil.read(pathToFile);
        if (dataRaw == null) {
            getLogger().error("Error while reading voxel chunk {}", pathToFile);
            return null;
        }
        IntBuffer data = MemoryUtil.memAllocInt(dataRaw.length);
        for (int i = 0; i < dataRaw.length; i++) {
            data.put(i, dataRaw[i]);
        }
        return new VoxelChunkStreamData(data);
    }

    @Override
    public AbstractResourceRef<?> newInstance(String key) {
        return new VoxelChunkResourceRef(key);
    }
}
