package com.pine.engine.repository.streaming;

import com.pine.common.injection.PBean;
import com.pine.engine.service.streaming.data.StreamData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@PBean
public class StreamingRepository {
    public final Map<String, AbstractResourceRef<?>> streamed = new ConcurrentHashMap<>();
    public final Map<String, StreamableResourceType> toStreamIn = new ConcurrentHashMap<>();

    public final Map<String, StreamData> streamData = new ConcurrentHashMap<>();
    public final Map<String, StreamableResourceType> discardedResources = new ConcurrentHashMap<>();
}
