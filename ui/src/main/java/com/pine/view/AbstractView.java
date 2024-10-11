package com.pine.view;

import com.pine.MetricCollector;
import com.pine.injection.PInject;
import com.pine.injection.PInjector;
import com.pine.panel.AbstractPanelContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbstractView extends MetricCollector implements View {
    protected final String id;
    protected final String imguiId;
    protected final List<View> children = new ArrayList<>();
    protected View parent;
    protected boolean visible = true;
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
    public View getParent() {
        return parent;
    }

    @Override
    public <T extends View> T appendChild(T child) {
        injector.inject(child);
        children.add(child);

        if (child.getContext() == null || this.getContext() != null) {
            child.setContext(this.getContext());
        }
        child.setParent(this);

        child.onInitialize();
        return child;
    }

    @Override
    public void removeChild(View child) {
        children.remove(child);
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void renderInternal() {
        for (View child : children) {
            child.render();
        }
    }

    @Override
    public void setParent(View parent) {
        this.parent = parent;
    }

    @Override
    public void render() {
        tick();
        if (!visible) {
            return;
        }
        renderInternal();
    }

    @Override
    public String getTitle() {
        return null;
    }
}

