package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.ViewDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbstractView implements View {
    private ViewDocument document;
    protected final String internalId;
    protected final String id;
    protected final List<View> children = new ArrayList<>();
    protected View parent;
    protected String innerText;
    protected boolean visible = true;

    public AbstractView(View parent, String id) {
        this.parent = parent;
        this.id = id;
        this.internalId = "##" + UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    public ViewDocument getDocument() {
        return document;
    }

    @Override
    public void setDocument(ViewDocument document) {
        if (this.document != null) {
            throw new RuntimeException("Document already bound to view");
        }

        this.document = document;
    }

    @Override
    public void onInitialize() {
    }

    @Override
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
    public void appendChild(View child) {
        document.appendChild(child, this);
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
    public void render() {
        for (View child : children) {
            child.render();
        }
    }

    public void setParent(View parent) {
        this.parent = parent;
    }
}

