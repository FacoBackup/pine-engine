package com.pine.common.resource;

import com.pine.app.Loggable;

public interface IResource extends Loggable {
    String getId();

    ResourceType getResourceType();
}
