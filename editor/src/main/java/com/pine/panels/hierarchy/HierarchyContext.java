package com.pine.panels.hierarchy;

import com.pine.core.panel.AbstractPanelContext;

import java.util.HashMap;
import java.util.Map;

public class HierarchyContext extends AbstractPanelContext {

    public final Map<String, String> searchMatchWith = new HashMap<>();
    public final Map<String, Byte> searchMatch = new HashMap<>();
    public final Map<String, Integer> opened = new HashMap<>();
}
