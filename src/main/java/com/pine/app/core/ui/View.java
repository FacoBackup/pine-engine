package com.pine.app.core.ui;


import com.pine.app.core.ui.panel.IPanelContext;

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

    IPanelContext getContext();

    void setInternalContext(IPanelContext internalContext);

    void setParent(View parent);

    void beforeRender();
}
