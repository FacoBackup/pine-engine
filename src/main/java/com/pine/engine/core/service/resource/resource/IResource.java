package com.pine.engine.core.service.resource.resource;

import com.pine.common.Loggable;

public interface IResource extends Loggable {
    String getId();

    ResourceType getResourceType();

    boolean isStatic();
}
