package com.pine.common.inspection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutableField {
    String label() default "-";

    String group() default "";

    String icon() default "";
}
