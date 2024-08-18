package com.jengine.app;

import com.jengine.app.engine.EngineRuntime;
import com.jengine.app.view.editor.WorldEditorWindow;
import com.jengine.app.view.core.service.WindowService;
import com.jengine.app.view.projects.ProjectsWindow;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class App implements SmartInitializingSingleton {

    @Autowired
    private WindowService windowService;

    @Autowired
    private EngineRuntime engine;

    @Override
    public void afterSingletonsInstantiated() {
        windowService.addWindow(new ProjectsWindow());
        try {
            engine.init();
        } catch (ResourceRuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
