package com.pine;

import org.apache.commons.io.input.Tailer;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;


public class PineEngine {
    private static boolean shouldStop = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(PineEngine.class);

    public static void main(String[] args) {
        createStopThread();

        PInjector injector = new PInjector(PineEngine.class.getPackageName());
        EditorWindow editorWindow = new EditorWindow();
        injector.inject(editorWindow);

        editorWindow.onInitialize();
        while (!GLFW.glfwWindowShouldClose(editorWindow.getHandle()) && !shouldStop) {
            try{
                editorWindow.render();
            }catch (Exception e){
                LOGGER.error(e.getMessage(), e);
            }
        }
        editorWindow.dispose();
        System.exit(0);
    }

    private static void createStopThread() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shouldStop = true));
    }
}
