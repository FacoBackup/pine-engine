package com.pine.repository.streaming;

import com.pine.injection.PBean;
import com.pine.service.streaming.StreamData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@PBean
public class StreamingRepository {
    public final Map<String, AbstractResourceRef<?>> loadedResources = new ConcurrentHashMap<>();
    public final Map<String, StreamableResourceType> scheduleToLoad = new ConcurrentHashMap<>();
    public final Map<String, StreamData> toLoadResources = new ConcurrentHashMap<>();
    public final Map<String, StreamableResourceType> discardedResources = new ConcurrentHashMap<>();
}
