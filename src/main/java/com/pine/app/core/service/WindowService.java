package com.pine.app.core.service;

import com.pine.app.core.repository.WindowRepository;
import com.pine.app.core.window.AbstractWindow;
import com.pine.app.core.window.WindowConfiguration;
import jakarta.annotation.PostConstruct;
import org.lwjgl.glfw.GLFW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WindowService {
    public static boolean shouldStop = false;
    public static AbstractWindow currentWindow;

    @Autowired
    private WindowRepository windowRepository;

    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shouldStop = true));
    }

    public static AbstractWindow getCurrentWindow() {
        return currentWindow;
    }

    public void addWindow(AbstractWindow app) {
        windowRepository.addWindow(app);
        currentWindow = app;
        app.setConfig(new WindowConfiguration(app.getWindowName(), app.getWindowWidth(), app.getWindowHeight(), app.isFullScreen()));
        app.onInitialize();
        while (!GLFW.glfwWindowShouldClose(app.getHandle()) && !shouldStop) {
            app.runFrame();
        }
        app.dispose();
    }
}
