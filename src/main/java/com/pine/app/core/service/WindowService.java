package com.pine.app.core.service;

import com.pine.app.core.repository.WindowInstance;
import com.pine.app.core.repository.WindowRepository;
import com.pine.app.core.window.AbstractWindow;
import com.pine.common.Loggable;
import jakarta.annotation.PostConstruct;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WindowService implements Loggable {
    public static boolean shouldStop = false;

    @Autowired
    private WindowRepository windowRepository;

    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shouldStop = true));
    }

    public void openWindow(Class<? extends AbstractWindow> windowClass) {
        if (WindowService.shouldStop) {
            return;
        }

        AbstractWindow window;
        try {
            window = windowClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate window class " + windowClass, e);
        }
        final var instances = windowRepository.getInstances();
        final String key = window.getClass().getCanonicalName();
        instances.put(key,
                new WindowInstance(new Thread(() -> {
                    window.onInitialize();
                    while (!GLFW.glfwWindowShouldClose(window.getHandle()) && !shouldStop) {
                        window.render();
                    }
                    window.dispose();
                    instances.remove(key);
                }), window));
        if (instances.containsKey(key)) {
            instances.get(key).thread().start();
        }
    }

    public void closeWindow(Class<? extends AbstractWindow> windowClass) {
        WindowInstance instance = windowRepository.getInstances().get(windowClass.getCanonicalName());
        if (instance != null) {
            GLFW.glfwWindowShouldClose(instance.window().getHandle());
        }
    }

    public void closeWindow(AbstractWindow window) {
        final String key = window.getClass().getCanonicalName();
        var instances = windowRepository.getInstances();
        if (instances.containsKey(key)) {
            instances.remove(key);
            if (instances.isEmpty()) {
                System.exit(0);
            }
        }
    }
}
