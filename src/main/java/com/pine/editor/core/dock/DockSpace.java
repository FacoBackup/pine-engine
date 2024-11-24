package com.pine.editor.core.dock;

import com.pine.editor.panels.console.ConsolePanel;
import com.pine.editor.panels.files.FilesPanel;
import com.pine.editor.panels.hierarchy.HierarchyPanel;
import com.pine.editor.panels.inspector.InspectorPanel;
import com.pine.editor.panels.metrics.MetricsPanel;
import com.pine.editor.panels.resources.ResourcesPanel;
import com.pine.editor.panels.viewport.ViewportPanel;
import com.pine.common.Icons;

import java.util.Arrays;

public enum DockSpace {
    Viewport("Viewport", Icons.ipublic, 0, 0, ViewportPanel.class),
    Hierarchy("Hierarchy", Icons.account_tree, HierarchyPanel.class),
    Inspector("Inspector", Icons.search, InspectorPanel.class),
    Console("Console", Icons.terminal, ConsolePanel.class),
    Files("Files", Icons.folder_open, FilesPanel.class),
    Resources("Resources", Icons.data_array, ResourcesPanel.class),
    Metrics("Metrics", Icons.bar_chart, MetricsPanel.class);

    private final String title;
    private final String codePoint;
    private final float paddingX;
    private final float paddingY;
    private final Class<? extends AbstractDockPanel> view;
    private static final String[] labels = new String[values().length];
    private Integer optionIndex = null;

    DockSpace(String title, String codePoint, Class<? extends AbstractDockPanel> view) {
        this(title, codePoint, -1, -1, view);
    }

    DockSpace(String title, String codePoint, float paddingX, float paddingY, Class<? extends AbstractDockPanel> view) {
        this.title = title;
        this.codePoint = codePoint;
        this.paddingX = paddingX;
        this.paddingY = paddingY;
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
}
