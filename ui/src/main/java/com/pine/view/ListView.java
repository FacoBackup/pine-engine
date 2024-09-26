package com.pine.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ListView extends AbstractView {
    protected List<? extends RepeatingViewItem> data = Collections.emptyList();
    protected final Map<String, View> containers = new HashMap<>();
    private Function<RepeatingViewItem, View> getView;

    @Override
    public void renderInternal() {
        for (RepeatingViewItem item : data) {
            getView(item, item.getKey()).render();
        }
    }

    protected View getView(RepeatingViewItem item, String key) {
        View container;
        if (containers.get(key) == null) {
            container = getView.apply(item);
            container.setContext(getContext());
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
