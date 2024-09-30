package com.pine.service.loader.impl.info;

import com.pine.SerializableRepository;
import com.pine.service.resource.resource.ResourceType;

/**
 * Used for defining limits in the load process, like, only load mesh number N
 */
public abstract class AbstractLoaderExtraInfo implements SerializableRepository {
    public abstract ResourceType getResourceType();
}
