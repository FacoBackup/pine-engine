package com.pine.dock;

import com.pine.view.View;

public interface DockDescription {
    String getTitle();

    String getIcon();

    float getPaddingY();

    float getPaddingX();

    Class<? extends AbstractDockPanel> getView();

    String[] getOptions();

    DockDescription getSelectedOption(int index);

    int getOptionIndex();

    DockDescription getDefault();
}
