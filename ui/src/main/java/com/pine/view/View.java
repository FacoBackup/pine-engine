package com.pine.view;


import com.pine.Renderable;
import com.pine.panel.AbstractPanelContext;

import java.util.List;

public interface View extends Renderable {

    void removeChild(View child);

    boolean isVisible();

    void setVisible(boolean visible);

    List<View> getChildren();

    View getParent();

    <T extends View> T appendChild(T child);

    AbstractPanelContext getContext();

    void setContext(AbstractPanelContext internalContext);

    void setParent(View parent);

    void renderInternal();
}
