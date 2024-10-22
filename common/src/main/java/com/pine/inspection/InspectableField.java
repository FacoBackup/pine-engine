package com.pine.inspection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface InspectableField {
    String label() default "-";

    String group() default "";

    int max() default Integer.MAX_VALUE;

    int min() default Integer.MIN_VALUE;

    boolean isAngle() default false;

    boolean isDirectChange() default false;

    String help() default "";

    boolean disabled() default false;
}
