package com.pine.app.core.ui.view;

import com.pine.app.core.ui.View;
import com.pine.app.core.ui.panel.AbstractPanel;
import imgui.ImGui;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class RepeatingView extends AbstractView {
    private List<? extends RepeatingViewItem> data = Collections.emptyList();
    private final Map<String, View> containers = new HashMap<>();
    private Function<RepeatingViewItem, View> getView;

    public RepeatingView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void render() {
        for (RepeatingViewItem item : data) {
            ImGui.beginGroup();
            String key = item.getKey();
            View container;
            if (containers.get(key) == null) {
                container = getView.apply(item);
                container.setDocument(getDocument());
                containers.put(key, container);
                container.onInitialize();
            } else {
                container = containers.get(key);
            }
            container.render();
            ImGui.endGroup();
        }
    }

    public void setGetView(Function<RepeatingViewItem, View> getView) {
        this.getView = getView;
    }

    public void setData(List<? extends RepeatingViewItem> data) {
        this.data = data;
    }
}
