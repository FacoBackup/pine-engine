package com.pine;

import com.pine.app.core.service.WindowService;
import com.pine.app.projects.ProjectsWindow;
import com.pine.engine.Engine;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class App implements SmartInitializingSingleton {
    private static ApplicationContext context;

    @Autowired
    private WindowService windowService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Engine engine;

    @PostConstruct
    private void init() {
        context = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        windowService.addWindow(new ProjectsWindow());
        try {
            engine.init();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public static ApplicationContext get() {
        return context;
    }
}
