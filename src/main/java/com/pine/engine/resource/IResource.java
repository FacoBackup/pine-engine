package com.pine.engine.resource;

import com.pine.common.Loggable;

public interface IResource extends Loggable {
    String getId();

    ResourceType getResourceType();
}
