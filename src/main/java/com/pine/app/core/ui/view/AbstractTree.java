package com.pine.app.core.ui.view;

import java.util.UUID;
import java.util.Vector;

public abstract class AbstractTree<T> {
    public final Vector<AbstractTree<T>> branches = new Vector<>();
    public final T data;
    public final String key;

    public AbstractTree(T data) {
        this.data = data;
        this.key = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
    }

    abstract public String getName();
}
