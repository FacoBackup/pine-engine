package com.pine.common.inspection;

import com.pine.common.Icons;

import java.lang.reflect.Method;
import java.util.UUID;

public class MethodDTO {
    private final String label;
    private final Method method;
    private final Object instance;
    private final ExecutableField delegate;

    public MethodDTO(ExecutableField delegate, Method method, Object instance) {
        this.delegate = delegate;
        this.label =  Icons.play_arrow + delegate.label() + "##" + UUID.randomUUID().toString().replaceAll("-", "");
        this.method = method;
        this.instance = instance;
    }

    public String getLabel() {
        return label;
    }

    public Method getMethod() {
        return method;
    }

    public Object getInstance() {
        return instance;
    }

    public String getGroup() {
        return delegate.group();
    }
}
