package com.pine;

import com.pine.engine.Engine;
import com.pine.app.view.core.service.WindowService;
import com.pine.app.view.projects.ProjectsWindow;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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
