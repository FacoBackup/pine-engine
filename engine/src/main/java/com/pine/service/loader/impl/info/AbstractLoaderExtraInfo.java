package com.pine.service.loader.impl.info;

import com.pine.SerializableRepository;
import com.pine.repository.streaming.StreamableResourceType;

/**
 * Used for defining limits in the load process, like, only load mesh number N
 */
public abstract class AbstractLoaderExtraInfo implements SerializableRepository {
    public abstract StreamableResourceType getResourceType();
}
