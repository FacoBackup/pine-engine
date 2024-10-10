package com.pine.repository.streaming;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.service.streaming.StreamLoadData;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@PBean
public class StreamingRepository implements SerializableRepository {
    public final List<AbstractStreamableResource<?>> streamableResources = new Vector<>();
    public transient final Map<String, AbstractStreamableResource<?>> schedule = new ConcurrentHashMap<>();
    public transient final Map<String, StreamLoadData> loadedResources = new ConcurrentHashMap<>();

}
