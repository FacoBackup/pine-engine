package com.pine.service.resource.resource;

import com.pine.Loggable;
import com.pine.injection.Disposable;

public interface IResource extends Loggable, Disposable {
    String getId();

    ResourceType getResourceType();

    boolean isStatic();

    void makeStatic();
}
