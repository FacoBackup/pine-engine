package com.jengine.app.view.core.service;

import com.jengine.app.view.core.repository.WindowRepository;
import com.jengine.app.view.core.window.WindowConfiguration;
import com.jengine.app.view.core.window.AbstractWindow;
import jakarta.annotation.PostConstruct;
import org.lwjgl.glfw.GLFW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WindowService {
    public static boolean shouldStop = false;

    @Autowired
    private WindowRepository windowRepository;

    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shouldStop = true));
    }

    public void addWindow(AbstractWindow app) {
        start(app);
        windowRepository.addWindow(app);
    }

    public void start(final AbstractWindow app) {
        initialize(app);
        app.preStart();
        while (!GLFW.glfwWindowShouldClose(app.getHandle()) && !shouldStop) {
            app.runFrame();
        }
        app.postStart();
        app.dispose();
    }

    private void initialize(final AbstractWindow app) {
        app.setConfig(new WindowConfiguration(app.getWindowName(), app.getWindowWidth(), app.getWindowHeight(), app.isFullScreen()));
        app.onInitialize();
    }
}
