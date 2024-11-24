package com.pine.service.resource;

import com.pine.injection.Disposable;
import com.pine.messaging.Loggable;

import java.util.UUID;

public abstract class AbstractEngineResource implements Loggable, Disposable {
    public final String id = UUID.randomUUID().toString();
}
