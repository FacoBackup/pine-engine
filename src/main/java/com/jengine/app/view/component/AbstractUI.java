package com.jengine.app.view.component;

import com.jengine.app.view.core.state.State;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractUI<T extends State<?>> {
    protected final List<AbstractUI<?>> children = new ArrayList<>();
    protected T state;

    public AbstractUI(T state, AbstractUI<?>... children) {
        this.state = state;
        this.children.addAll(List.of(children));
    }

    public T getState() {
        return state;
    }

    public void setState(T state) {
        this.state = state;
    }

    public void addChild(AbstractUI<?> child) {
        children.add(child);
    }

    public abstract void render();
}
