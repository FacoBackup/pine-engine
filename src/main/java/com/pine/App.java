package com.pine;

import com.pine.app.core.service.WindowService;
import com.pine.app.projects.ProjectsWindow;
import com.pine.engine.Engine;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class App implements SmartInitializingSingleton {

    @Autowired
    private WindowService windowService;

    @Autowired
    private Engine engine;

    @Override
    public void afterSingletonsInstantiated() {
        windowService.openWindow(new ProjectsWindow());
        try {
            engine.init();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
