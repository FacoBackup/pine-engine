package com.pine.app.view.component.view;

import com.pine.app.view.component.View;
import com.pine.app.view.component.panel.AbstractPanel;
import jakarta.annotation.Nullable;
import org.springframework.lang.NonNull;

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

    @Override
    public View getParent() {
        return parent;
    }

    @Override
    public void onInitialize() {
    }

    public void appendChild(View child) {
        panel.appendChild(child, this);
    }

    @NonNull
    public <T extends AbstractView> T appendChild(Class<T> child) {
        var instance = instantiate(child, this, null, panel);
        if (instance != null) {
            appendChild(instance);
        } else {
            throw new RuntimeException("Cannot instantiate child class " + child);
        }
        return instance;
    }

    @Nullable
    static public <T extends AbstractView> T instantiate(Class<T> clazz, View parent, String id, AbstractPanel panel) {
        try {
            return clazz.getConstructor(View.class, String.class, AbstractPanel.class).newInstance(parent, id, panel);
        } catch (Exception ex) {
            return null;
        }
    }

    public void setInnerText(String textContent) {
        innerText = textContent;
    }

    @Override
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

    @Override
    public void render(long index) {
        for (View child : children) {
            child.render(index);
            index++;
        }
    }


}
