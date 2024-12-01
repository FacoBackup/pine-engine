package com.pine.engine.service.resource;

import com.pine.common.injection.Disposable;
import com.pine.common.messaging.Loggable;

import java.util.UUID;

public abstract class AbstractEngineResource implements Loggable, Disposable {
    public final String id = UUID.randomUUID().toString();
}
