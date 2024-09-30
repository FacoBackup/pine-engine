package com.pine.dock;

import com.pine.panel.AbstractPanelContext;

import java.io.Serializable;

public interface DockDescription extends Serializable {
    String getTitle();

    String getIcon();

    float getPaddingY();

    float getPaddingX();

    Class<? extends AbstractDockPanel> getView();

    String[] getOptions();

    DockDescription getSelectedOption(int index);

    int getOptionIndex();

    DockDescription getDefault();

    Class<? extends AbstractPanelContext> getContext();
}
