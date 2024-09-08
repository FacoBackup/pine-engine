package com.pine.engine.core.service.loader.impl.info;

import com.pine.engine.core.service.resource.resource.ResourceType;

/**
 * Used for defining limits in the load process, like, only load mesh number N
 */
public interface ILoaderExtraInfo {

    ResourceType getResourceType();
}
