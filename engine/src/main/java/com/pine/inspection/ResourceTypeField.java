package com.pine.inspection;

import com.pine.service.resource.LocalResourceType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceTypeField {
    LocalResourceType type();
}
