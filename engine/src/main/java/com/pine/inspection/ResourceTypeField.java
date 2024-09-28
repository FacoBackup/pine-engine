package com.pine.inspection;

import com.pine.service.resource.resource.ResourceType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceTypeField {
    ResourceType type();
}
