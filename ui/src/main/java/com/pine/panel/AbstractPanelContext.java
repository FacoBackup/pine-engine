package com.pine.panel;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPanelContext {
    private final List<Runnable> callbacks = new ArrayList<>();

    public Runnable subscribe(Runnable callback){
        callbacks.add(callback);
        return () -> callbacks.remove(callback);
    }

    public void onChange(){
        callbacks.forEach(Runnable::run);
    }
}
