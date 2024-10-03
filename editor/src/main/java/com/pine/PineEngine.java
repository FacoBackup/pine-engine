package com.pine;

import com.pine.injection.PInjector;
import com.pine.window.EditorWindow;


public class PineEngine {
    private static final PInjector INJECTOR = new PInjector(PineEngine.class.getPackageName());

    public static void main(String[] args) {
        WindowService.windowImpl = EditorWindow.class;
        INJECTOR.boot();
        System.exit(0);
    }
}
