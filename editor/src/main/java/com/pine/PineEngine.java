package com.pine;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PineEngine {
    public static boolean shouldStop = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(PineEngine.class);
    private static final PInjector INJECTOR = new PInjector(PineEngine.class.getPackageName());
    private static final EditorWindow EDITOR_WINDOW = new EditorWindow();

    public static void main(String[] args) {
        createStopThread();
        INJECTOR.inject(EDITOR_WINDOW);
        startWindow();
        EDITOR_WINDOW.dispose();
        System.exit(0);
    }

    private static void startWindow() {
        EDITOR_WINDOW.onInitialize();
        while (!GLFW.glfwWindowShouldClose(EDITOR_WINDOW.getHandle()) && !shouldStop) {
            try {
                EDITOR_WINDOW.render();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private static void createStopThread() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shouldStop = true));
    }
}
