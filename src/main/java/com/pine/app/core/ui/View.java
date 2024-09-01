package com.pine.app.core.ui;


import java.util.List;

public interface View extends Renderable {

    String getInnerText();

    boolean isVisible();

    void setVisible(boolean visible);

    List<View> getChildren();

    String getId();

    View getParent();

    void appendChild(View child);

    ViewDocument getDocument();

    void setDocument(ViewDocument document);
}
