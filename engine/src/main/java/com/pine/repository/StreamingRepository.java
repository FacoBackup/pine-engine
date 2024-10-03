package com.pine.repository;

import com.pine.SerializableRepository;
import com.pine.injection.PBean;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;

import java.util.ArrayList;
import java.util.List;

@PBean
public class StreamingRepository implements SerializableRepository {
    public final List<AbstractLoaderResponse> loadedResources = new ArrayList<>();
}
