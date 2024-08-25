package com.pine.core.service.common;

import com.pine.app.Loggable;
import com.pine.core.service.ResourceType;

public interface IResource extends Loggable {
    String getId();

    ResourceType getResourceType();
}
