package com.pine;

import com.pine.window.EditorWindow;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PineEngine {
    private static final PInjector INJECTOR = new PInjector(PineEngine.class.getPackageName());

    public static void main(String[] args) {
        INJECTOR.reset();
        WindowService windowService = (WindowService) INJECTOR.getBean(WindowService.class);
        EditorWindow editorWindow = new EditorWindow();
        INJECTOR.inject(editorWindow);
        editorWindow.onInitialize();
        windowService.setWindowImpl(editorWindow);
        windowService.start();
        System.exit(0);
    }
}
