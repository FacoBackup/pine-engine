package com.pine.service.resource.resource;

import com.pine.Disposable;
import com.pine.Loggable;

public interface IResource extends Loggable, Disposable {
    String getId();

    ResourceType getResourceType();

    boolean isStatic();

    void makeStatic();
}
