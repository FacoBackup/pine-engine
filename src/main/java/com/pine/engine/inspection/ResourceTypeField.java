package com.pine.engine.inspection;

import com.pine.engine.repository.streaming.StreamableResourceType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceTypeField {
    StreamableResourceType type();
}
