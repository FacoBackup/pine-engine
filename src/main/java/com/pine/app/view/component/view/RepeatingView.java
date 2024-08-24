package com.pine.app.view.component.view;

import com.pine.app.view.component.View;
import com.pine.app.view.component.panel.AbstractPanel;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RepeatingView extends AbstractView {
    private List<? extends RepeatingViewItem> data = Collections.emptyList();
    private final Map<String, View> containers = new HashMap<>();
    private Function<RepeatingViewItem, View> getView;

    public RepeatingView(View parent, String id, AbstractPanel panel) {
        super(parent, id, panel);
    }

    @Override
    public void render(long index) {
        for (int i = 0; i < data.size(); i++) {
            RepeatingViewItem item = data.get(i);
            String key = item.getKey();
            View container;
            if (containers.get(key) == null) {
                container = getView.apply(item);
                containers.put(key, container);
                container.onInitialize();
            }else{
                container = containers.get(key);
            }
            container.render(index+i);
            index++;
        }
    }

    public void setGetView(Function<RepeatingViewItem, View> getView) {
        this.getView = getView;
    }

    public void setData(List<? extends RepeatingViewItem> data) {
        this.data = data;
    }
}
