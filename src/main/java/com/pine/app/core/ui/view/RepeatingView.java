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
    protected List<? extends RepeatingViewItem> data = Collections.emptyList();
    protected final Map<String, View> containers = new HashMap<>();
    private Function<RepeatingViewItem, View> getView;

    public RepeatingView(View parent, String id) {
        super(parent, id);
    }

    @Override
    public void render() {
        if(!visible){
            return;
        }
        for (RepeatingViewItem item : data) {
            String key = item.getKey();
            View container;
            container = getView(item, key);
            container.render();
        }
    }

    protected View getView(RepeatingViewItem item, String key) {
        View container;
        if (containers.get(key) == null) {
            container = getView.apply(item);
            container.setDocument(getDocument());
            containers.put(key, container);
            container.onInitialize();
        } else {
            container = containers.get(key);
        }
        return container;
    }

    public void setGetView(Function<RepeatingViewItem, View> getView) {
        this.getView = getView;
    }

    public void setData(List<? extends RepeatingViewItem> data) {
        this.data = data;
    }
}
