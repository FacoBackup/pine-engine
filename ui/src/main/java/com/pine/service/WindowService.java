package com.pine.service;

import com.pine.Loggable;
import com.pine.repository.WindowInstance;
import com.pine.repository.WindowRepository;
import com.pine.window.AbstractWindow;
import jakarta.annotation.PostConstruct;
import org.lwjgl.glfw.GLFW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WindowService implements Loggable {
    public static boolean shouldStop = false;
    public static Long runningWindow;

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
                    beginLoop(window);
                    endLoop(window, instances, key);
                }), window));
        if (instances.containsKey(key)) {
            instances.get(key).thread().start();
        }
    }

    private static void endLoop(AbstractWindow window, Map<String, WindowInstance> instances, String key) {
        window.dispose();
        instances.remove(key);
        runningWindow = null;
    }

    private static void beginLoop(AbstractWindow window) {
        boolean shouldRun = true;
        while (shouldRun && !shouldStop) {
            if (runningWindow == null) {
                window.onInitialize();
                runningWindow = window.getHandle();
            } else if (runningWindow == window.getHandle()) {
                shouldRun = !GLFW.glfwWindowShouldClose(window.getHandle());
                if(shouldRun) {
                    window.render();
                }
            }
        }
    }

    public void closeWindow(Class<? extends AbstractWindow> windowClass) {
        WindowInstance instance = windowRepository.getInstances().get(windowClass.getCanonicalName());
        if (instance != null) {
            GLFW.glfwSetWindowShouldClose(instance.window().getHandle(), true);
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
