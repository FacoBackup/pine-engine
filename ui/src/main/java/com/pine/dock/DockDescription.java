package com.pine.dock;

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
}
