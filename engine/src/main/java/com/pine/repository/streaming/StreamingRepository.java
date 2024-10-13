package com.pine.repository.streaming;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.service.streaming.StreamLoadData;
import com.pine.service.streaming.mesh.MeshStreamableResource;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@PBean
public class StreamingRepository implements SerializableRepository {
    public final List<AbstractStreamableResource<?>> streamableResources = new Vector<>();
    public transient final Map<String, AbstractStreamableResource<?>> schedule = new ConcurrentHashMap<>();
    public transient final Map<String, StreamLoadData> loadedResources = new ConcurrentHashMap<>();

    @Nullable
    public AbstractStreamableResource<?> getById(String resourceId) {
        return streamableResources.stream().filter(a -> a.id.equals(resourceId)).findFirst().orElse(null);
    }
}
