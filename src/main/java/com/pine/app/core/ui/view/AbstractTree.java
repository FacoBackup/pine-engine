package com.pine.app.core.ui.view;

import java.util.UUID;
import java.util.Vector;

public abstract class AbstractTree<T, R> {
    public final Vector<AbstractTree<T, R>> branches = new Vector<>();
    public final T data;
    public final String key;
    public final Vector<R> extraData = new Vector<>();

    public AbstractTree(T data) {
        this.data = data;
        this.key = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
    }

    abstract public String getName();
}
