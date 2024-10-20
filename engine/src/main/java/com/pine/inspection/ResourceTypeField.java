package com.pine.inspection;

import com.pine.repository.streaming.StreamableResourceType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceTypeField {
    StreamableResourceType type();
}
