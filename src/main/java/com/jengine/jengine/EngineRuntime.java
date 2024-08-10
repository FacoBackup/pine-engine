package com.jengine.jengine;

import com.jengine.jengine.app.editor.EditorRuntimeWindow;
import com.jengine.jengine.app.editor.RuntimeWindow;
import com.jengine.jengine.window.WindowService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EngineRuntime {

    @Autowired
    private WindowService windowService;

    @PostConstruct
    public void init() {
        windowService.addWindow(new EditorRuntimeWindow());
    }
}
