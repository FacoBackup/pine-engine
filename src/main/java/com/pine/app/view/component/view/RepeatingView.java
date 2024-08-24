package com.pine.app.view.component.view;

import com.pine.app.view.component.View;
import com.pine.app.view.component.panel.AbstractPanel;
import imgui.ImGui;

import java.util.*;
import java.util.function.Function;

public class RepeatingView extends AbstractView {
    private List<? extends RepeatingViewItem> data = Collections.emptyList();
    private final Map<String, View> containers = new HashMap<>();
    private Function<RepeatingViewItem, View> getView;
    private String title;

    public RepeatingView(View parent, String id, AbstractPanel panel) {
        super(parent, id, panel);
    }

    @Override
    public void render(long index) {
        ImGui.beginGroup();
        for (int i = 0; i < data.size(); i++) {
            ImGui.beginGroup();
            ImGui.sameLine();
            RepeatingViewItem item = data.get(i);
            String key = item.getKey();
            View container;
            if (containers.get(key) == null) {
                container = getView.apply(item);
                containers.put(key, container);
                container.onInitialize();
            } else {
                container = containers.get(key);
            }
            container.render(index + i);
            index++;
            ImGui.endGroup();
        }
        ImGui.endGroup();

    }

    public void setGetView(Function<RepeatingViewItem, View> getView) {
        this.getView = getView;
    }

    public void setData(List<? extends RepeatingViewItem> data) {
        this.data = data;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
