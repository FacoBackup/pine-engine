package com.pine.component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EnumSelection {
    Class<? extends SelectableEnum> enumType();
}
