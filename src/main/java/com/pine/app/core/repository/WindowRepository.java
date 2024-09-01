package com.pine.app.core.repository;

import com.pine.app.core.window.AbstractWindow;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class WindowRepository {
    private AbstractWindow currentWindow;

    public void setCurrentWindow(AbstractWindow currentWindow) {
        this.currentWindow = currentWindow;
    }

    public AbstractWindow getCurrentWindow() {
        return currentWindow;
    }
}
