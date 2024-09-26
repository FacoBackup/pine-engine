package com.pine.ui;


import com.pine.Renderable;
import com.pine.ui.panel.AbstractPanelContext;

import java.util.List;

public interface View extends Renderable {

    void removeChild(View child);

    String getInnerText();

    void setInnerText(String textContent);

    boolean isVisible();

    void setVisible(boolean visible);

    List<View> getChildren();

    View getParent();

    <T extends View> T appendChild(T child);

    ViewDocument getDocument();

    void setDocument(ViewDocument document);

    AbstractPanelContext getContext();

    void setContext(AbstractPanelContext internalContext);

    void setParent(View parent);

    void renderInternal();
}
