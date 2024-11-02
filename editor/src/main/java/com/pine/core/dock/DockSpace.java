package com.pine.core.dock;

import com.pine.core.panel.AbstractPanelContext;
import com.pine.panels.console.ConsolePanel;
import com.pine.panels.files.ContentBrowser;
import com.pine.panels.hierarchy.HierarchyPanel;
import com.pine.panels.inspector.InspectorPanel;
import com.pine.panels.metrics.MetricsPanel;
import com.pine.panels.resources.ResourcesPanel;
import com.pine.panels.viewport.ViewportContext;
import com.pine.panels.viewport.ViewportPanel;
import com.pine.theme.Icons;

import java.util.Arrays;

public enum DockSpace {
    Viewport("Viewport", Icons.ipublic, 0, 0, ViewportPanel.class, ViewportContext.class),
    Hierarchy("Hierarchy", Icons.account_tree, HierarchyPanel.class, null),
    Inspector("Inspector", Icons.search, InspectorPanel.class, null),
    Console("Console", Icons.terminal, ConsolePanel.class, null),
    Files("Files", Icons.folder_open, ContentBrowser.class, null),
    Resources("Resources", Icons.data_array, ResourcesPanel.class, null),
    Metrics("Metrics", Icons.bar_chart, MetricsPanel.class, null);

    private final String title;
    private final String codePoint;
    private final float paddingX;
    private final float paddingY;
    private final Class<? extends AbstractDockPanel> view;
    private final Class<? extends AbstractPanelContext> context;
    private static final String[] labels = new String[values().length];
    private Integer optionIndex = null;

    DockSpace(String title, String codePoint, Class<? extends AbstractDockPanel> view, Class<? extends AbstractPanelContext> context) {
        this(title, codePoint, -1, -1, view, context);
    }

    DockSpace(String title, String codePoint, float paddingX, float paddingY, Class<? extends AbstractDockPanel> view, Class<? extends AbstractPanelContext> context) {
        this.title = title;
        this.codePoint = codePoint;
        this.paddingX = paddingX;
        this.paddingY = paddingY;
        this.context = context;
        this.view = view;
    }

    public float getPaddingX() {
        return paddingX;
    }

    public float getPaddingY() {
        return paddingY;
    }

    public String getIcon() {
        return codePoint;
    }

    public String getTitle() {
        return title;
    }

    public Class<? extends AbstractDockPanel> getView() {
        return view;
    }

    public String[] getOptions() {
        if (labels[0] == null) {
            DockSpace[] values = values();
            for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
                var value = values[i];
                labels[i] = value.codePoint + " " + value.title;
            }
        }
        return labels;
    }

    public DockSpace getSelectedOption(int index) {
        return values()[index];
    }

    public int getOptionIndex() {
        if (optionIndex == null) {
            optionIndex = Arrays.asList(values()).indexOf(this);
        }
        return optionIndex;
    }

    public DockSpace getDefault() {
        return DockSpace.Files;
    }

    public Class<? extends AbstractPanelContext> getContext() {
        return context;
    }
}