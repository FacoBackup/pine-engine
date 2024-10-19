package com.pine.repository.streaming;

import com.pine.injection.PBean;
import com.pine.service.streaming.StreamData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@PBean
public class StreamingRepository {
    public final Map<String, AbstractResourceRef<?>> streamableResources = new ConcurrentHashMap<>();
    public final Map<String, StreamableResourceType> schedule = new ConcurrentHashMap<>();
    public final Map<String, StreamData> loadedResources = new ConcurrentHashMap<>();
    public final Map<String, StreamableResourceType> failedStreams = new ConcurrentHashMap<>();
}
