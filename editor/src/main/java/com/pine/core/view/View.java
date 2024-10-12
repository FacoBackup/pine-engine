package com.pine.core.view;


import com.pine.Renderable;
import com.pine.core.panel.AbstractPanelContext;

import java.util.List;

public interface View extends Renderable {

    void removeChild(View child);

    List<View> getChildren();

    <T extends View> T appendChild(T child);

    AbstractPanelContext getContext();

    void setContext(AbstractPanelContext internalContext);
}
