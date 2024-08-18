package com.jengine.app.view.core.repository;

import com.jengine.app.view.core.window.AbstractWindow;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class WindowRepository {
    private final Map<String, AbstractWindow> windows = new HashMap<>();

    public <T extends AbstractWindow> void addWindow(T window) {
        windows.put(window.getClass().getName(), window);
    }

    public AbstractWindow findWindow(Class<? extends AbstractWindow> clazz) {
        return windows.get(clazz.getName());
    }
}
