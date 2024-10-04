package com.pine.repository.streaming;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.service.streaming.StreamLoadData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PBean
public class StreamingRepository implements SerializableRepository {
    public final List<AbstractStreamableResource<?>> streamableResources = new ArrayList<>();

    public transient final Map<String, AbstractStreamableResource<?>> schedule = new HashMap<>();
    public transient final Map<String, StreamLoadData> loadedResources = new HashMap<>();

}
