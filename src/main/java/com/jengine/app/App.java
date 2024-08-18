package com.jengine.app;

import com.jengine.app.core.EngineRuntime;
import com.jengine.app.view.editor.EditorRuntimeWindow;
import com.jengine.app.view.core.WindowService;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
public class App implements SmartInitializingSingleton {

    @Autowired
    private WindowService windowService;

    @Autowired
    private EngineRuntime engine;

    @Override
    public void afterSingletonsInstantiated() {
        windowService.addWindow(new EditorRuntimeWindow());
        try {
            engine.init();
        } catch (ResourceRuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
