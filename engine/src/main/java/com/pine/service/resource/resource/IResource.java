package com.pine.service.resource.resource;

import com.pine.Loggable;

public interface IResource extends Loggable {
    String getId();

    ResourceType getResourceType();

    boolean isStatic();

    void makeStatic();
}
