package com.pine.core.view;

import com.pine.MetricCollector;
import com.pine.core.panel.AbstractPanelContext;
import com.pine.injection.PInject;
import com.pine.injection.PInjector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbstractView extends MetricCollector implements View {
    protected final String id;
    protected final String imguiId;
    protected final List<View> children = new ArrayList<>();
    private AbstractPanelContext internalContext;

    @PInject
    public PInjector injector;

    public AbstractView() {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.imguiId = "##" + id;
    }

    @Override
    public AbstractPanelContext getContext() {
        return internalContext;
    }

    @Override
    public void setContext(AbstractPanelContext internalContext) {
        this.internalContext = internalContext;
    }

    @Override
    public List<View> getChildren() {
        return children;
    }

    @Override
    public <T extends View> T appendChild(T child) {
        injector.inject(child);
        children.add(child);
        child.setContext(this.getContext());
        child.onInitialize();
        return child;
    }

    @Override
    public void removeChild(View child) {
        children.remove(child);
    }

    @Override
    public void render() {
        for (View child : children) {
            child.render();
        }
    }

    @Override
    public String getTitle() {
        return null;
    }
}

