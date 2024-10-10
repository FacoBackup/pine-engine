package com.pine.service.resource;

import com.pine.injection.Disposable;
import com.pine.messaging.Loggable;

public interface IResource extends Loggable, Disposable {
    String getId();

    LocalResourceType getResourceType();

    boolean isStatic();

    void makeStatic();
}
