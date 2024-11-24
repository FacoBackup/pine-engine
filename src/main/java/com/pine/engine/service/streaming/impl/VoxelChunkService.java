package com.pine.engine.service.streaming.impl;

import com.pine.FSUtil;
import com.pine.common.injection.PBean;
import com.pine.engine.repository.streaming.AbstractResourceRef;
import com.pine.engine.repository.streaming.StreamableResourceType;
import com.pine.engine.service.streaming.data.StreamData;
import com.pine.engine.service.streaming.data.VoxelChunkStreamData;
import com.pine.engine.service.streaming.ref.VoxelChunkResourceRef;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.Map;

@PBean
public class VoxelChunkService extends AbstractStreamableService<VoxelChunkResourceRef> {
    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.VOXEL_CHUNK;
    }

    @Override
    public StreamData stream(String pathToFile, Map<String, StreamableResourceType> schedule, Map<String, AbstractResourceRef<?>> streamableResources) {
        var dataRaw = (int[]) FSUtil.readBinary(pathToFile);
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
