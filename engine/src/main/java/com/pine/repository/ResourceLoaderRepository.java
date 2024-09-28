package com.pine.repository;

import com.pine.PBean;
import com.pine.SerializableRepository;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;

import java.util.ArrayList;
import java.util.List;

@PBean
public class ResourceLoaderRepository implements SerializableRepository {
    public final List<AbstractLoaderResponse> loadedResources = new ArrayList<>();
}
