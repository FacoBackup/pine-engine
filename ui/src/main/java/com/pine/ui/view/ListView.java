package com.pine.ui.view;

import com.pine.ui.View;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ListView extends AbstractView {
    protected List<? extends RepeatingViewItem> data = Collections.emptyList();
    protected final Map<String, View> containers = new HashMap<>();
    private Function<RepeatingViewItem, View> getView;

    public ListView(View parent, String id) {
        super(parent, id);
    }

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
            container.setDocument(getDocument());
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
