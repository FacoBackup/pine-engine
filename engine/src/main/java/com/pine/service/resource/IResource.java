package com.pine.service.resource;

import com.pine.Loggable;
import com.pine.injection.Disposable;

public interface IResource extends Loggable, Disposable {
    String getId();

    LocalResourceType getResourceType();

    boolean isStatic();

    void makeStatic();
}
