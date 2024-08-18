package com.jengine.app.view.component.view;

import com.jengine.app.view.component.View;
import com.jengine.app.view.component.panel.AbstractPanel;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class RepeatingView<T> extends AbstractView {
    private List<T> rows = Collections.emptyList();
    private BiConsumer<T, Long> renderRow;
    private Runnable onInitialize;
    private boolean initialized = false;

    public RepeatingView(View parent, String id, AbstractPanel panel) {
        super(parent, id, panel);
    }

    @Override
    public void render(long index) {
        if (!initialized) {
            if (onInitialize != null) {
                onInitialize.run();
            }
            initialized = true;
        }

        if (!visible || renderRow == null) {
            return;
        }

        for (int i = 0; i < rows.size(); i++) {
            renderRow.accept(rows.get(i), i + index);
        }
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setOnInitialize(Runnable onInitialize) {
        this.onInitialize = onInitialize;
    }

    public void setRenderRow(BiConsumer<T, Long> renderRow) {
        this.renderRow = renderRow;
    }
}
