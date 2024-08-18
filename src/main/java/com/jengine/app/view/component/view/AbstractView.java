package com.jengine.app.view.component.view;

import com.jengine.app.view.component.View;
import com.jengine.app.view.component.panel.AbstractPanel;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractView implements View {
    protected final String id;
    protected final List<View> children = new ArrayList<>();
    protected final View parent;
    private final AbstractPanel panel;
    protected String innerText;
    protected boolean visible = true;

    public AbstractView(View parent, String id, AbstractPanel panel) {
        this.panel = panel;
        this.parent = parent;
        this.id = id;
    }

    public AbstractPanel getPanel() {
        return panel;
    }

    public List<View> getChildren() {
        return children;
    }

    @Override
    public String getId() {
        return id;
    }

    public View getParent() {
        return parent;
    }

    @Override
    public void onInitialize() {
    }

    public void appendChild(View child) {
        panel.appendChild(child, this);
    }

    public void setInnerText(String textContent) {
        innerText = textContent;
    }

    public String getInnerText() {
        return innerText;
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
    public View getElementById(String id) {
        return panel.getElementById(id);
    }

}
