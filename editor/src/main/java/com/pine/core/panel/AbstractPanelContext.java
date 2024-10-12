package com.pine.core.panel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPanelContext implements Serializable {
    private transient List<Runnable> callbacks = new ArrayList<>();

    public Runnable subscribe(Runnable callback) {
        if (callbacks == null) {
            callbacks = new ArrayList<>();
        }
        callbacks.add(callback);
        return () -> callbacks.remove(callback);
    }

    public void onChange() {
        callbacks.forEach(Runnable::run);
    }
}
