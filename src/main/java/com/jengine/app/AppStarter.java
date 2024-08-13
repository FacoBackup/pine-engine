package com.jengine.app;

import com.jengine.app.view.editor.EditorRuntimeWindow;
import com.jengine.app.view.core.WindowService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppStarter {

    @Autowired
    private WindowService windowService;

    @PostConstruct
    public void init() {
        windowService.addWindow(new EditorRuntimeWindow());
    }
}
