package com.pine.app.core.service;

import com.pine.app.core.repository.WindowRepository;
import com.pine.app.core.window.AbstractWindow;
import com.pine.app.core.window.WindowConfiguration;
import com.pine.common.Loggable;
import jakarta.annotation.PostConstruct;
import org.lwjgl.glfw.GLFW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WindowService implements Loggable {
    public static boolean shouldStop = false;

    @Autowired
    private WindowRepository windowRepository;

    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shouldStop = true));
    }

    public AbstractWindow getCurrentWindow() {
        return windowRepository.getCurrentWindow();
    }

    public void openWindow(AbstractWindow window) {
        windowRepository.setCurrentWindow(window);
        window.setConfig(new WindowConfiguration(window.getWindowName(), window.getWindowWidth(), window.getWindowHeight(), window.isFullScreen()));
        window.onInitialize();
        while (!GLFW.glfwWindowShouldClose(window.getHandle()) && !shouldStop) {
            window.runFrame();
        }
        window.dispose();
        windowRepository.setCurrentWindow(null);
    }

    public void closeWindow() {
        GLFW.glfwWindowShouldClose(getCurrentWindow().getHandle());
    }
}
