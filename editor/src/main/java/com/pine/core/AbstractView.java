package com.pine.core;

import com.pine.Renderable;
import com.pine.injection.PInject;
import com.pine.injection.PInjector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbstractView implements Renderable {
    protected final String id;
    protected final String imguiId;
    protected final List<AbstractView> children = new ArrayList<>();

    @PInject
    public PInjector injector;

    public AbstractView() {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.imguiId = "##" + id;
    }

    public List<AbstractView> getChildren() {
        return children;
    }

    public <T extends AbstractView> T appendChild(T child) {
        injector.inject(child);
        children.add(child);
        child.onInitialize();
        return child;
    }

    public void removeChild(AbstractView child) {
        children.remove(child);
    }

    @Override
    public void render() {
        for (AbstractView child : children) {
            child.render();
        }
    }

    public void onRemove(){}
}

