package com.pine.inspection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NumericFieldRule {
    int max();

    int min();

    boolean isAngle();

    boolean isDirectChange();

}
